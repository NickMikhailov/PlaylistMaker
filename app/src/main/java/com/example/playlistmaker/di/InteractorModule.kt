import com.example.playlistmaker.library.domain.db.FavoritesInteractor
import com.example.playlistmaker.library.domain.impl.FavoritesInteractorImpl
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.sharing.SharingInteractor
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.impl.domain.impl.SharingInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val interactorModule = module {

    factory<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    factory<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    factory<SharingInteractor>{
        SharingInteractorImpl(get())
    }
    factory<SettingsInteractor>{
        SettingsInteractorImpl(get())
    }
    factory<ExternalNavigator>{
        ExternalNavigatorImpl(androidContext())
    }
    single<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }
}