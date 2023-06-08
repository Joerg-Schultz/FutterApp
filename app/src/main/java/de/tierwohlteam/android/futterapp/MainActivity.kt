package de.tierwohlteam.android.futterapp

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.futterapp.databinding.MainActivityBinding
import de.tierwohlteam.android.futterapp.fragments.FutterAppFragmentFactory
import de.tierwohlteam.android.futterapp.viewModels.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var fragmentFactory: FutterAppFragmentFactory

    private lateinit var binding: MainActivityBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.fragmentFactory = fragmentFactory
        setTheme(R.style.Theme_FutterApp)
        //setContentView(R.layout.main_activity)
        binding = MainActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
/*        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
 */
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val emptyDatabaseDialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.empty_database))
            .setMessage(getString(R.string.empty_database_warning))
            .setIcon(R.drawable.ic_warning)
            .setPositiveButton(getString(R.string.yes)) {_,_ ->
                mainViewModel.emptyDatabase()
            }
            .setNegativeButton(getString(R.string.no)) {_, _ ->
                /* NO-OP */
            }
        .create()
        emptyDatabaseDialog.setOnShowListener {
            emptyDatabaseDialog.getButton(DialogInterface.BUTTON_NEGATIVE)?.let {
                it.setTextColor(ContextCompat.getColor(this, R.color.black))
                it.setBackgroundColor(ContextCompat.getColor(this, R.color.accent))
            }
        }

        return when (item.itemId) {
            R.id.miEmptyDatabase -> {
                emptyDatabaseDialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}