package sreich.countthedays


import android.Manifest
import android.annotation.TargetApi
import android.app.Fragment
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageItemInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceActivity
import android.support.v7.app.ActionBar
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.preference.RingtonePreference
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.mcxiaoke.koi.ext.dateNow
import com.mcxiaoke.koi.ext.longToast
import com.mcxiaoke.koi.log.logi
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element
import org.joda.time.DateTime
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.ObjectOutputStream

/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 *
 * See [
   * Android Design: Settings](http://developer.android.com/design/patterns/settings.html) for design guidelines and the [Settings
   * API Guide](http://developer.android.com/guide/topics/ui/settings.html) for more information on developing a Settings UI.
 */
class SettingsActivity : AppCompatPreferenceActivity() {
    override fun onOptionsItemSelected(item: MenuItem) =
            if (item.itemId == android.R.id.home) {
                super.onBackPressed()
                true
            } else super.onOptionsItemSelected(item)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()
    }

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    private fun setupActionBar() = supportActionBar?.setDisplayHomeAsUpEnabled(true)

    override fun onIsMultiPane() = isXLargeTablet(this)

    override fun onBuildHeaders(target: List<PreferenceActivity.Header>) =
            loadHeadersFromResource(R.xml.pref_headers, target)

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    override fun isValidFragment(fragmentName: String) =
            when (fragmentName) {
                PreferenceFragment::class.java.name,
                GeneralPreferenceFragment::class.java.name,
                DataSyncPreferenceFragment::class.java.name,
                AboutPreferenceFragment::class.java.name
                -> true
                else -> false
            }


    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    class GeneralPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_general)
            setHasOptionsMenu(true)

            // Bind the summaries of EditText/List/Dialog preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("example_text"))
        }

        override fun onOptionsItemSelected(item: MenuItem) =
                when (id) {
                    android.R.id.home -> {
                        startActivity(Intent(activity, SettingsActivity::class.java))
                        true
                    }
                    else -> super.onOptionsItemSelected(item)
                }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    class DataSyncPreferenceFragment : PreferenceFragment() {
        val appContext by lazy { activity.applicationContext!! }
        val prefs by lazy { activity.getSharedPreferences("settings", MODE_PRIVATE)!! }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_data_sync)
            setHasOptionsMenu(true)

            // Bind the summaries of EditText/List/Dialog preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("sync_frequency"))

            val importBackupPref = findPreference(getString(R.string.importBackup))
            importBackupPref.setOnPreferenceClickListener {
                val readPermissionGranted = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) ==  PackageManager.PERMISSION_GRANTED;
                if (readPermissionGranted){
                    // Everything is good!
                    // Bring up the file browser
                    //Log.v(">>>>", "READ Permission GRANTED");
                    importBackupIntent()
                }
                else {
                    // We do not have reading permission
                    // Request it
                    //
                    // TODO: Implement callback onRequestPermissionsResult (Problem: Higher API-Version is required)
                    //       Now it is a bit inconvinient for the user, because he has to click the Import Backup button twice
                    //       The first time he has to grant the reading permission, the second time the file browser opens
                    // TODO: Inform the user what is happening
                    //Log.v(">>>>", "READ Permission NOT granted");
                    val REQUEST_READ = 0;
                    val test = Manifest.permission.READ_EXTERNAL_STORAGE.toString();
                    ActivityCompat.requestPermissions(activity, Array(test.length, {test}), REQUEST_READ);
                }
                true
            }


            val exportBackupPref = findPreference(getString(R.string.exportBackup))
            exportBackupPref.setOnPreferenceClickListener {
                exportBackupIntent()
                true
            }

        }




        private val REQUEST_BACKUP_IMPORT = 1

        private fun importBackupIntent() {
            // Check if we have the reading permission

            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                // Using MIME type "text/plain" doesn't let you select the file
                // Use */* so all files could be selected
                //type = "text/plain"
                //type = "application/octet-stream"
                type = "*/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }

            // Only the system receives the ACTION_OPEN_DOCUMENT, so no need to test.
            startActivityForResult(intent, REQUEST_BACKUP_IMPORT)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (resultCode != RESULT_OK || data == null || data.data == null) {
                return
            }

            when (requestCode) {
                REQUEST_BACKUP_IMPORT -> {
                    Log.i(this::class.java.simpleName, "import backup attempt, uri: ${data.data} ")
                    val inputStream = appContext.contentResolver.openInputStream(data.data)
                    importBackup(inputStream)
                }
            }
        }

        private fun InputStream.readText() = readBytes().toString(Charsets.UTF_8)
        private fun importBackup(input: InputStream) {
            val json = input.readText()
            //todo we should validate we can parse this before storing, but i'm a bit lazy right now
            Log.i(this::class.java.simpleName, "importing string input: $json")
            prefs.edit().apply {
                putString(MainActivity.Settings.settingsJsonKey, json)
                apply()
            }

            longToast(i18n(R.string.settingsImportedToast))
        }

        private fun exportBackupIntent() {
            val jsonSettingsText = prefs.getString(MainActivity.Settings.settingsJsonKey, null)
            if (jsonSettingsText == null) {
                longToast(i18n(R.string.settingsExportNotAvailableToast))
            }

            val fileUri = saveBackup()

            val sharingIntent = Intent(android.content.Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(android.content.Intent.EXTRA_STREAM, fileUri)
                startActivity(Intent.createChooser(this, i18n(R.string.settubgsShareBackupTo)))
            }
        }

        private fun saveBackup(): Uri {
            val dateTime = DateTime.now().toDateTimeISO().toString()
            val fileName = "Count The Days-$dateTime.backup"

            val jsonSettingsText = prefs.getString(MainActivity.Settings.settingsJsonKey, null)

            val backupFile = File(appContext.cacheDir, fileName)
            backupFile.writeText(jsonSettingsText)

            // wrap File object into a content provider
            val fileUri = FileProvider.getUriForFile(appContext, "sreich.countthedays.fileprovider", backupFile)

            return fileUri
        }

        override fun onOptionsItemSelected(item: MenuItem) =
                if (item.itemId == android.R.id.home) {
                    startActivity(Intent(activity, SettingsActivity::class.java))
                    true
                } else super.onOptionsItemSelected(item)

    }

    class AboutPreferenceFragment : Fragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
//            addPreferencesFromResource(R.xml.pref_about)
            setHasOptionsMenu(true)

            // Bind the summaries of EditText/List/Dialog preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //           bindPreferenceSummaryToValue(findPreference("example_text"))
        }

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
            val viewGroup = inflater!!.inflate(R.xml.pref_about, container, false) as ViewGroup

            val versionElement = Element().apply {
                title = "Version ${BuildConfig.VERSION_NAME}"
            }

            val buildTimeElement = Element().apply {
                title = "Build time ${BuildConfig.BUILD_TIME}"
            }

            val aboutPage = AboutPage(activity).apply {
                isRTL(false)
//                    .setImage(R.drawable.abc_action_bar_item_background_material)
                setDescription(i18n(R.string.aboutDescription))
                addItem(versionElement)
                addItem(buildTimeElement)
                addGroup(i18n(R.string.aboutGroup))
                addEmail(i18n(R.string.aboutEmail))
                addGitHub(i18n(R.string.aboutGithub))
                addPlayStore(BuildConfig.APPLICATION_ID)
            }.create()

            viewGroup.addView(aboutPage)

            return viewGroup
        }

        override fun onOptionsItemSelected(item: MenuItem) =
                when (item.itemId) {
                    android.R.id.home -> {
                        startActivity(Intent(activity, SettingsActivity::class.java))
                        true
                    }

                    else -> super.onOptionsItemSelected(item)
                }
    }

    companion object {
        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         */
        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
            val stringValue = value.toString()

            when (preference) {
                is ListPreference -> {
                    // For list preferences, look up the correct display value in
                    // the preference's 'entries' list.
                    val listPreference = preference
                    val index = listPreference.findIndexOfValue(stringValue)

                    val summary = if (index >= 0) {
                        listPreference.entries[index]
                    } else {
                        null
                    }

                    // Set the summary to reflect the new value.
                    preference.summary = summary
                }
                else -> // For all other preferences, set the summary to the value's
                    // simple string representation.
                    preference.summary = stringValue
            }
            true
        }

        /**
         * Helper method to determine if the device has an extra-large screen. For
         * example, 10" tablets are extra-large.
         */
        private fun isXLargeTablet(context: Context) =
                context.resources.configuration.screenLayout and
                        Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE

        /**
         * Binds a preference's summary to its value. More specifically, when the
         * preference's value is changed, its summary (line of text below the
         * preference title) is updated to reflect the value. The summary is also
         * immediately updated upon calling this method. The exact display format is
         * dependent on the type of preference.

         * @see .sBindPreferenceSummaryToValueListener
         */
        private fun bindPreferenceSummaryToValue(preference: Preference) {
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                                                                     PreferenceManager
                                                                             .getDefaultSharedPreferences(
                                                                                     preference.context)
                                                                             .getString(preference.key, ""))
        }
    }
}
