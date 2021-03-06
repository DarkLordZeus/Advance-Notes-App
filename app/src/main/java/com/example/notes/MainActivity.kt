package com.example.notes

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.example.notes.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var navController : NavController
    private lateinit var AppBarConfiguration : AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Notes)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        ActivityCompat.requestPermissions(this@MainActivity,
            arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE),
            111)
        val navHostFragment=supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController=navHostFragment.navController

        AppBarConfiguration=AppBarConfiguration(
            setOf(R.id.homeFragment,R.id.backgroundcolor,R.id.recycleBinFragment,R.id.favourites_fragment),binding.DrawerLayout
        )

        setupActionBarWithNavController(navController,AppBarConfiguration)
        //binding.toolbar.setupWithNavController(navController)
        binding.Sidenav.setupWithNavController(navController)
        binding.toolbar.overflowIcon?.setTint(ContextCompat.getColor(this, R.color.black))

    }
    override fun onSupportNavigateUp() : Boolean
    {
        return navController.navigateUp(AppBarConfiguration)||super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?):Boolean
    {
        menuInflater.inflate(R.menu.toolbar_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem):Boolean{
        return when(item.itemId){

            else -> item.onNavDestinationSelected(navController)||super.onOptionsItemSelected(item)
        }

    }


}