
import com.example.playlistmaker.library.data.db.PlaylistDbConverter
import com.example.playlistmaker.library.data.db.impl.FavoritesRepositoryImpl
import com.example.playlistmaker.library.data.db.TrackDbConverter
import com.example.playlistmaker.library.data.db.impl.PlaylistRepositoryImpl
import com.example.playlistmaker.library.domain.db.FavoritesRepository
import com.example.playlistmaker.library.domain.db.PlaylistRepository
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
    single { TrackDbConverter() }
    single { PlaylistDbConverter() }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }
    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(),get(),get())
    }
}

