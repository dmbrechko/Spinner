package com.example.spinner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.spinner.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainList = mutableListOf<Person>()
    private val filteredList = mutableListOf<Person>()
    private val roles = listOf("Engineer", "Mechanic", "Programmer", "Security")
    private val filterNotSet = "No filter..."
    private val filter = listOf(filterNotSet, *roles.toTypedArray())
    private lateinit var mainListAdapter: ArrayAdapter<Person>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.apply {
            setSupportActionBar(toolbar)
            saveBTN.setOnClickListener {
                if (nameET.text.isBlank() || lastNameET.text.isBlank()) {
                    makeToast(getString(R.string.enter_name_and_last_name))
                    return@setOnClickListener
                }
                val age: Int
                try {
                    age = ageET.text.toString().toInt()
                    if (age < 18 || age > 80) {
                        makeToast(getString(R.string.people_can_t_be_employees_at_this_age))
                        return@setOnClickListener
                    }
                } catch (e: NumberFormatException) {
                    makeToast(getString(R.string.enter_valid_age))
                    return@setOnClickListener
                }
                val person = Person(
                    nameET.text.toString(),
                    lastNameET.text.toString(),
                    age,
                    roleS.selectedItem as String
                )
                mainList.add(person)
                updateFilteredList()
                nameET.text.clear()
                lastNameET.text.clear()
                ageET.text.clear()
            }
            val filterAdapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_spinner_item,
                filter).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
            filterS.adapter = filterAdapter
            filterS.setSelection(0)
            filterS.onItemSelectedListener = object: OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                    updateFilteredList()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    // do nothing
                }
            }
            val rolesAdapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_spinner_item,
                roles).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
            roleS.adapter = rolesAdapter
            roleS.setSelection(0)
            mainListAdapter = object: ArrayAdapter<Person>(this@MainActivity, R.layout.list_item, filteredList) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    var row = convertView
                    if (row == null) {
                        val inflater = LayoutInflater.from(parent.context)
                        row = inflater.inflate(R.layout.list_item, parent, false)
                    }
                    val user = getItem(position)
                    row?.findViewById<TextView>(R.id.nameTV)?.text = String
                        .format("Name: %s", user?.name)
                    row?.findViewById<TextView>(R.id.last_nameTV)?.text = String
                        .format("Last name: %s", user?.lastName)
                    row?.findViewById<TextView>(R.id.roleTV)?.text = String
                        .format("Role: %s", user?.role)
                    row?.findViewById<TextView>(R.id.ageTV)?.text = String
                        .format("Age: %s", user?.age.toString())
                    row?.tag = user
                    return row!!
                }
            }
            listLV.adapter = mainListAdapter
            listLV.setOnItemClickListener { _, view, _, _ ->
                val person = view.tag as Person
                mainList.remove(person)
                updateFilteredList()
            }
        }
    }

    private fun updateFilteredList() {
        binding.apply {
            val filterSelection = filterS.selectedItem as String
            filteredList.clear()
            if ((filterSelection) != filterNotSet) {
                filteredList.addAll(mainList.filter { it.role == filterSelection })
            } else {
                filteredList.addAll(mainList)
            }
            mainListAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_exit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_exit -> {
                moveTaskToBack(true)
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}

data class Person(
    val name: String,
    val lastName: String,
    val age: Int,
    val role: String
)