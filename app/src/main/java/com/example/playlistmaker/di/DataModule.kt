import android.content.Context
import androidx.room.Room
import com.example.playlistmaker.library.data.db.AppDatabase
import com.example.playlistmaker.main.data.shared_prefs.AppSharedPreferences
import com.example.playlistmaker.search.data.network.ITunesSearchAPI
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ITunesSearchAPI> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesSearchAPI::class.java)
    }
    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }
    single {
        androidContext()
            .getSharedPreferences("local_storage", Context.MODE_PRIVATE)
    }

    single {
        AppSharedPreferences(androidContext())
    }

    factory { Gson() }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }
}