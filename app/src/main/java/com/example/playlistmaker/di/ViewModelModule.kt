import com.example.playlistmaker.library.ui.view_model.FavoritesViewModel
import com.example.playlistmaker.library.ui.view_model.PlaylistsViewModel
import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.ui.view_model.TrackSearchViewModel
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        TrackSearchViewModel(get(),get())
    }
    viewModel {(track: Track) ->
        PlayerViewModel(track, get())
    }
    viewModel {
        SettingsViewModel(get(),get())
    }
    viewModel {
        FavoritesViewModel(get(),get())
    }
    viewModel {
        PlaylistsViewModel()
    }
}
