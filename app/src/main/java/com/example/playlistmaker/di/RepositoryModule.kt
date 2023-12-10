
import com.example.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.TracksRepository
import com.example.playlistmaker.settings.data.SettingsRepository
import com.example.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }
    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }
}
//import org.koin.android.ext.android.inject
//
//class MusicActivity: AppCompatActivity() {
//    private val repository: MusicRepository by inject()
//
//    ...
//}
//val repositoryModule = module {
//    // Указали, что создаём реализацию интерфейса MusicRepository
//    single<MusicRepository> {
//        MusicRepositoryImpl(get(), get())
//    }
//}
//
//val repositoryModule = module {
//    single {
//        MusicRepositoryImpl(get(), get())
//    } bind MusicRepository::class
//}
//
//val repositoryModule = module {
//    single {
//        MusicRepositoryImpl(get(), get())
//    } binds(arrayOf(MusicRepository::class, Player::class))
//}
//
//class MusicViewModel(
//    // Добавили дополнительный аргумент
//    private val currentTrackId: Int,
//    private val repository: MusicRepository
//): ViewModel() {
//    ...
//}
//
//
//val viewModelModule = module {
//    // Добавили аргумент, передачу которого ожидаем
//    viewModel { (trackId: Int) ->
//        MusicViewModel(trackId, get())
//    }
//}
//
//
//import org.koin.androidx.viewmodel.ext.android.viewModel
//
//class MusicActivity: AppCompatActivity() {
//    private val viewModel: MusicViewModel by viewModel()
//
//    ...
//}
