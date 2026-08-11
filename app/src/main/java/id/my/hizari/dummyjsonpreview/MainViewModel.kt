package id.my.hizari.dummyjsonpreview

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.hizari.dummyjsonpreview.di.AppModule
import javax.inject.Inject
import javax.inject.Named

/**
 * id.my.hizari.dummyjsonpreview
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@HiltViewModel
class MainViewModel @Inject constructor(
    @param:Named(AppModule.NAME_APP_LABEL) val appLabel: String
) : ViewModel()
