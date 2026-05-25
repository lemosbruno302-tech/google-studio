package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.data.IptvDatabase
import com.example.data.IptvRepository
import com.example.ui.IptvApp
import com.example.ui.IptvViewModel
import com.example.ui.IptvViewModelFactory

class MainActivity : ComponentActivity() {
  private val database by lazy { IptvDatabase.getDatabase(this) }
  private val repository by lazy { IptvRepository(database) }
  private val viewModel: IptvViewModel by viewModels {
    IptvViewModelFactory(repository)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      IptvApp(viewModel = viewModel)
    }
  }
}
