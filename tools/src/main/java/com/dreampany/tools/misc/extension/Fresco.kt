package com.dreampany.tools.misc.extension

import androidx.core.net.toUri
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequestBuilder

/**
 * Created by roman on 26/4/20
 * Copyright (c) 2020 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */


/*
fun SimpleDraweeView.setUrl(url: String): SimpleDraweeView {
    val uri = url.toUri()
    val request =
        ImageRequestBuilder.newBuilderWithSource(uri)
            //.setResizeOptions(ResizeOptions(width, height))
            .build()
    this.setController(
        Fresco.newDraweeControllerBuilder()
            .setOldController(getController())
            .setImageRequest(request)
            //.setTapToRetryEnabled(retryEnabled)
            .build()
    )

    return this
}*/
