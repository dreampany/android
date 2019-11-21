package com.dreampany.tools.ui.misc

import com.dreampany.framework.data.enums.Action
import com.dreampany.framework.data.enums.State
import com.dreampany.framework.data.enums.Subtype
import com.dreampany.framework.data.enums.Type
import com.dreampany.framework.data.model.Request
import com.dreampany.tools.data.model.RelatedQuiz
import com.dreampany.tools.misc.Constants

/**
 * Created by Roman-372 on 7/5/2019
 * Copyright (c) 2019 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
class RelatedQuizRequest(
    type: Type = Type.DEFAULT,
    subtype: Subtype = Subtype.DEFAULT,
    state: State = State.DEFAULT,
    var resolve: State = State.DEFAULT,
    action: Action = Action.DEFAULT,
    single: Boolean = Constants.Default.BOOLEAN,
    important: Boolean = Constants.Default.BOOLEAN,
    progress: Boolean = Constants.Default.BOOLEAN,
    input: RelatedQuiz? = Constants.Default.NULL,
    var given: String? = Constants.Default.NULL
) : Request<RelatedQuiz>(
    type = type,
    subtype = subtype,
    state = state,
    action = action,
    single = single,
    important = important,
    progress = progress,
    input = input
) {

}