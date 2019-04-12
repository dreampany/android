package com.dreampany.frame.ui.activity

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Parcelable
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.afollestad.aesthetic.*
import com.codemybrainsout.ratingdialog.RatingDialog
import com.dreampany.frame.R
import com.dreampany.frame.app.BaseApp
import com.dreampany.frame.data.model.Color
import com.dreampany.frame.data.model.Task
import com.dreampany.frame.misc.AppExecutors
import com.dreampany.frame.misc.Constants
import com.dreampany.frame.ui.callback.UiCallback
import com.dreampany.frame.ui.fragment.BaseFragment
import com.dreampany.frame.util.AndroidUtil
import com.dreampany.frame.util.FragmentUtil
import com.dreampany.frame.util.NotifyUtil
import com.dreampany.frame.util.TextUtil
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.tapadoo.alerter.Alerter
import dagger.Lazy
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject


/**
 * Created by Hawladar Roman on 5/22/2018.
 * BJIT Group
 * hawladar.roman@bjitgroup.com
 */
@Suppress("UNCHECKED_CAST")
abstract class BaseActivity :
        DaggerAppCompatActivity(),
        UiCallback<BaseActivity, BaseFragment, Task<*>, ViewModelProvider.Factory, ViewModel>,
        MultiplePermissionsListener,
        PermissionRequestErrorListener {

    @Inject
    internal lateinit var ex: AppExecutors
    protected lateinit var binding: ViewDataBinding
    protected var task: Task<*>? = null
    protected var childTask: Task<*>? = null
    protected var currentFragment: BaseFragment? = null
    protected var color: Color? = null
    protected var fireOnStartUi: Boolean = true
    private var ratingDialog: RatingDialog? = null
    private var progress: ProgressDialog? = null

    open fun getLayoutId(): Int {
        return 0
    }

    open fun getToolbarId(): Int {
        return R.id.toolbar
    }

    open fun isFullScreen(): Boolean {
        return false
    }

    open fun isHomeUp(): Boolean {
        return true
    }

    open fun isScreenOn(): Boolean {
        return false
    }

    open fun hasRemoteUi(): Boolean {
        return false
    }

    open fun hasColor(): Boolean {
        return getApp().hasColor()
    }

    open fun applyColor(): Boolean {
        return getApp().applyColor()
    }

    open fun hasTheme(): Boolean {
        return getApp().hasTheme()
    }

    open fun getAppTheme(): Int {
        return R.style.AppTheme
    }

    open fun hasBackPressed(): Boolean {
        return false
    }

    open fun hasRatePermitted(): Boolean {
        return false;
    }

    open fun getScreen() : String {
        return javaClass.simpleName
    }

    protected abstract fun onStartUi(state: Bundle?)

    protected abstract fun onStopUi()

    override fun onCreate(savedInstanceState: Bundle?) {
        val app = getApp();
        if (AndroidUtil.hasLollipop()) {
            requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        }
        if (hasTheme()) {
            Aesthetic.attach(this)
        }
        super.onCreate(savedInstanceState)
        if (isScreenOn()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        if (hasColor()) {
            color = app.getColor()
        }
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        val layoutId = getLayoutId()
        if (layoutId != 0) {
            initLayout(layoutId)
            initToolbar()
            if (hasTheme()) {
                initTheme()
            }
        }
        if (fireOnStartUi) {
            onStartUi(savedInstanceState)
            getApp().throwAnalytics(Constants.Event.ACTIVITY, getScreen())
        }

        if (app.hasRate() && hasRatePermitted()) {
            startRate()
        }
    }

    override fun onResume() {
        super.onResume()
        if (hasTheme()) {
            Aesthetic.resume(this)
        }
    }

    override fun onPause() {
        if (hasTheme()) {
            Aesthetic.pause(this)
        }
        super.onPause()
    }

    override fun onDestroy() {
        val app = getApp();
        if (app.hasRate()) {
            stopRate()
        }
        hideAlert()
        hideProgress()
        onStopUi()
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (hasBackPressed()) {
            return
        }

        val fragment = currentFragment
        if (fragment != null && fragment.hasBackPressed()) {
            return
        }
/*        val manager = supportFragmentManager;
        if (manager.getBackStackEntryCount() > 0) {
            manager.popBackStack();
        }*/
        super.onBackPressed()
    }

/*    @Nullable
    @Override
    public String key() {
        return "base";
    }*/

    override fun getUiActivity(): BaseActivity {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUiFragment(): BaseFragment {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun set(t: Task<*>) {
        childTask = t
    }

    override fun get(): ViewModelProvider.Factory {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getX(): ViewModel {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPermissionsChecked(report: MultiplePermissionsReport) {

    }

    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {

    }

    override fun onError(error: DexterError) {

    }

    fun isAlive(): Boolean {
        return AndroidUtil.isAlive(this)
    }

    fun getApp(): BaseApp {
        return application as BaseApp
    }

    open fun getUiColor(): Color? {
        return color
    }

    private fun initLayout(layoutId: Int) {
        if (isFullScreen()) {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            //BarUtil.hide(this)
        } else {
            //BarUtil.show(this)
        }
        binding = DataBindingUtil.setContentView(this, layoutId)
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(getToolbarId())
        if (toolbar != null) {
            if (isFullScreen()) {
                if (toolbar.isShown) {
                    toolbar.visibility = View.GONE
                }
            } else {
                if (!toolbar.isShown) {
                    toolbar.visibility = View.VISIBLE
                }
                setSupportActionBar(toolbar)
                if (isHomeUp()) {
                    val actionBar = supportActionBar
                    if (actionBar != null) {
                        actionBar.setDisplayHomeAsUpEnabled(true)
                        actionBar.setHomeButtonEnabled(true)
                    }
                }
            }
        }
    }

    private fun initTheme() {
        if (Aesthetic.isFirstTime || true) {
            Aesthetic.config {
                //colorPrimaryRes(R.color.colorPrimary)
                //colorPrimaryDarkRes(R.color.colorPrimaryDark)
                //colorAccentRes(R.color.colorAccent)
                //colorWindowBackgroundRes(R.color.material_grey200)
                //textColorPrimaryRes(R.color.white)
                //textColorPrimaryInverseRes(android.R.color.white)
                //textColorSecondary(R.color.material_grey100)
                //textColorSecondaryInverseRes(R.color.material_grey800)
                //colorStatusBarAuto()
                //colorNavigationBarAuto()

                //lightStatusBarMode(AutoSwitchMode.AUTO)
                //lightNavigationBarMode(AutoSwitchMode.AUTO)

                //tabLayoutBackgroundMode(ColorMode.PRIMARY)
                //tabLayoutIndicatorMode(ColorMode.ACCENT)
                //navigationViewMode(NavigationViewMode.SELECTED_ACCENT)
                //bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY)
                //bottomNavigationIconTextMode(BottomNavIconTextMode.SELECTED_ACCENT)
                swipeRefreshLayoutColorsRes(
                        R.color.material_green700,
                        R.color.material_red700,
                        R.color.material_yellow700)
            }
        }
    }

    protected fun <T : Task<*>> getCurrentTask(freshTask: Boolean): T? {
        if (task == null || freshTask) {
            task = getIntentValue<T>(Task::class.java.simpleName)
        }
        return task as T?
    }

    protected fun <T> getIntentValue(key: String): T? {
        val bundle = getBundle()
        return getIntentValue<T>(key, bundle)
    }

    protected fun <T> getIntentValue(key: String, bundle: Bundle?): T? {
        var t: T? = null
        if (bundle != null) {
            t = bundle.getParcelable<Parcelable>(key) as T?
        }
        if (bundle != null && t == null) {
            t = bundle.getSerializable(key) as T?
        }
        return t
    }

    protected fun getBundle(): Bundle? {
        return intent.extras
    }

    override fun setTitle(titleId: Int) {
        if (titleId <= 0) {
            return
        }
        setSubtitle(TextUtil.getString(this, titleId))
    }

    fun setSubtitle(subtitleId: Int) {
        if (subtitleId <= 0) {
            return
        }
        setSubtitle(TextUtil.getString(this, subtitleId))
    }

    fun setTitle(title: String?) {
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = title
        }
    }

    fun setSubtitle(subtitle: String?) {
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.subtitle = subtitle
        }
    }

    fun openActivity(target: Class<*>) {
        AndroidUtil.openActivity(this, target)
    }

    fun openActivity(target: Class<*>, requestCode: Int) {
        AndroidUtil.openActivity(this, target, requestCode)
    }

    fun openActivity(target: Class<*>, task: Task<*>) {
        AndroidUtil.openActivity(this, target, task)
    }

/*    fun openActivitySerializable(target: Class<BaseActivity>, task: Task<*>) {
        AndroidUtil.openActivitySerializable(this, target, task)
    }*/

/*    protected <T extends BaseFragment> T commitFragment(final Class<T> fragmentClass, final int parentId) {
        T currentFragment = FragmentUtil.commitFragment(this, fragmentClass, parentId);
        setCurrentFragment(currentFragment);
        return currentFragment;
    }*/

    protected fun <T : BaseFragment> commitFragment(clazz: Class<T>, fragmentProvider: Lazy<T>, parentId: Int): T {
/*        val manager = getSupportFragmentManager()
        val transaction = manager?.beginTransaction();
        val current = manager?.primaryNavigationFragment
        if (current != null) {
            transaction?.detach(current);
        }
        var fragment: T? = FragmentUtil.getFragmentByTag(this, clazz.simpleName)
        if (fragment == null) {
            Timber.v("New Fragment Created %s", clazz)
            fragment = fragmentProvider.get()
            transaction?.add(parentId, fragment, clazz.simpleName)
        } else {
            transaction?.attach(fragment)
        }
        this.currentFragment = fragment
        transaction?.setPrimaryNavigationFragment(fragment)
        transaction?.setReorderingAllowed(true)
        transaction?.commitNowAllowingStateLoss()
        return currentFragment as T*/
        var fragment: T? = FragmentUtil.getFragmentByTag(this, clazz.simpleName)
        if (fragment == null) {
            //Timber.v("New Fragment Created %s", clazz)
            fragment = fragmentProvider.get()
        }
        val currentFragment = FragmentUtil.commitFragment<T>(this, fragment, parentId)
        this.currentFragment = currentFragment
        return currentFragment
    }

    protected fun <T : BaseFragment> commitFragment(clazz: Class<T>, fragmentProvider: Lazy<T>, parentId: Int, task: Task<*>): T {
        var fragment: T? = FragmentUtil.getFragmentByTag(this, clazz.simpleName)
        if (fragment == null) {
            fragment = fragmentProvider.get()
            val bundle = Bundle()
            bundle.putParcelable(Task::class.java.simpleName, task)
            fragment!!.arguments = bundle
        } else {
            fragment.arguments!!.putParcelable(Task::class.java.simpleName, task)
        }

        val currentFragment = FragmentUtil.commitFragment(this, fragment, parentId)
        this.currentFragment = currentFragment
        return currentFragment
    }

    fun checkPermissions(vararg permissions: String, listener: MultiplePermissionsListener) {
        if (!isAlive()) {
            return
        }
        Dexter.withActivity(this)
                .withPermissions(*permissions)
                .withListener(listener)
                .check()
    }

    fun showInfo(info: String) {
        if (!isAlive()) {
            return
        }
        NotifyUtil.showInfo(this, info)
    }

    fun showError(error: String) {
        if (!isAlive()) {
            return
        }
        NotifyUtil.showInfo(this, error)
    }

    fun showAlert(title: String, text: String, @ColorRes backgroundColor: Int, timeout: Long) {
        showAlert(title, text, backgroundColor, timeout, null)
    }

    fun showAlert(title: String,
                  text: String,
                  @ColorRes backgroundColor: Int,
                  timeout: Long,
                  listener: View.OnClickListener?) {
        hideAlert()
       val alerter = Alerter.create(this)
                .setTitle(title)
                .setText(text)
                .setBackgroundColorRes(backgroundColor)
                .setIcon(R.drawable.alerter_ic_face)
                .setDuration(timeout)
                .enableSwipeToDismiss();
        if (listener != null) {
            alerter.setOnClickListener(listener)
        }
        alerter.show()
    }

    fun hideAlert() {
        if (Alerter.isShowing) {
            Alerter.hide()
        }
    }

    fun showProgress(message: String) {
        if (progress == null) {
            progress = ProgressDialog(this)
            progress?.setCancelable(true)
        }

        if (progress?.isShowing()!!) {
            return
        }
        progress?.let {
            it.setMessage(message + "...")
            it.show()
        }
    }

    fun hideProgress() {
        progress?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }

    private fun startRate() {
        if (ratingDialog == null || !ratingDialog!!.isShowing) {
            ratingDialog = RatingDialog.Builder(this)
                    .threshold(3f)
                    .session(7)
                    .onRatingBarFormSumbit({

                    }).build()
            ratingDialog?.show()
        }

    }

    private fun stopRate() {
        ratingDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }
}