package com.dreampany.tools.injector.data

import com.dreampany.framework.injector.data.FrameModule
import com.dreampany.tools.injector.vm.ViewModelModule
import com.dreampany.translation.injector.TranslationModule
import dagger.Module


/**
 * Created by Hawladar Roman on 29/5/18.
 * Dreampany Ltd
 * dreampanymail@gmail.com
 */

@Module(
    includes = [
        FrameModule::class,
        TranslationModule::class,
        DatabaseModule::class,
        ApiModule::class,
        MediaModule::class,
        NoteModule::class,
        WordModule::class,
        VpnModule::class,
        RadioModule::class,
        CryptoModule::class,
        SupportModule::class,
        ViewModelModule::class
    ]
)
class BuildersModule {
}
