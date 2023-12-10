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
        PlayerViewModel(track)  //надо понять откуда брать track
    }
    viewModel {
        SettingsViewModel(get(),get())  //надо понять откуда брать track
    }
}
