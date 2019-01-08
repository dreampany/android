package com.dreampany.frame.util;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dreampany.frame.R;
import com.dreampany.frame.ui.adapter.SmartAdapter;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

import java.util.Objects;

import eu.davidea.flexibleadapter.helpers.EmptyViewHelper;

/**
 * Created by Hawladar Roman on 5/24/2018.
 * Dreampany Ltd
 * dreampanymail@gmail.com
 */
public final class ViewUtil {

    private ViewUtil() {
    }

    public static <T extends View> T getViewById(Fragment fragment, int viewId) {
        return getViewById(fragment.getView(), viewId);
    }

    public static <T extends View> T getViewById(View parentView, int viewId) {
        return (parentView == null || viewId <= 0) ? null : parentView.findViewById(viewId);
    }

    public static void visible(Fragment parent, @IdRes int viewId) {
        View view = getViewById(parent, viewId);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void hide(Fragment parent, @IdRes int viewId) {
        View view = getViewById(parent, viewId);
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    public static void setClickListener(View parentView, int viewId, View.OnClickListener clickListener) {
        setClickListener(getViewById(parentView, viewId), clickListener);
    }

    public static void setClickListener(View view, View.OnClickListener clickListener) {
        if (view != null) {
            view.setOnClickListener(clickListener);
        }
    }


    public static void setClickListener(Fragment fragment, @IdRes int viewId) {
        setClickListener(getViewById(fragment.getView(), viewId), (View.OnClickListener) fragment);
    }

    public static void setText(Fragment fragment, @IdRes int viewId, @StringRes int textId) {
        setText(fragment, viewId, fragment.getString(textId));
    }

    public static void setFancyText(Fragment fragment, @IdRes int viewId, @StringRes int textId) {
        setFancyText(fragment, viewId, fragment.getString(textId));
    }

    public static void setText(Fragment fragment, @IdRes int viewId, String text) {
        TextView view = getViewById(fragment.getView(), viewId);
        setText(view, text);
    }

    public static void setFancyText(Fragment fragment, @IdRes int viewId, String text) {
        MaterialFancyButton view = getViewById(fragment.getView(), viewId);
        setFancyText(view, text);
    }

    public static void setText(TextView view, String text) {
        if (view != null) {
            view.setText(text);
        }
    }

    public static void setTextColor(TextView view,@ColorInt int color) {
        if (view != null) {
            view.setTextColor(color);
        }
    }

    public static void setFancyText(MaterialFancyButton view, String text) {
        if (view != null) {
            view.setText(text);
        }
    }

    public static void setFabBackgroundTint(FloatingActionButton fab, int color) {
        ColorStateList fabColorStateList = new ColorStateList(
                new int[][]{
                        new int[]{}
                },
                new int[]{
                        color,
                }
        );
        fab.setBackgroundTintList(fabColorStateList);
    }


    public static void setBackground(final View view, final int colorId) {
        if (FloatingActionButton.class.isInstance(view)) {
            Runnable runnable = () -> ((FloatingActionButton) view).setBackgroundTintList(ColorStateList.valueOf(ColorUtil.getColor(view.getContext(), colorId)));
            view.post(runnable);
        } else if (ImageView.class.isInstance(view)) {
            /*Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    ((ImageView) view).setImageResource(resourceId);
                }
            };
            AndroidUtil.post(runnable);*/
        } else if (View.class.isInstance(view)) {
            view.setBackgroundColor(ColorUtil.getColor(view.getContext(), colorId));
        }
    }

    public static void setSwipe(SwipeRefreshLayout swipe, SwipeRefreshLayout.OnRefreshListener listener) {
        if (swipe != null) {
            swipe.setColorSchemeResources(
                    R.color.colorPrimary,
                    R.color.colorAccent,
                    R.color.colorPrimaryDark);
            swipe.setOnRefreshListener(listener);
        }
    }

    public static void setRecycler(@NonNull SmartAdapter adapter,
                                   @NonNull RecyclerView recycler,
                                   @NonNull RecyclerView.LayoutManager layout) {
        ViewUtil.setRecycler(adapter, recycler, layout, null);
    }

    public static void setRecycler(@NonNull SmartAdapter adapter,
                                   @NonNull RecyclerView recycler,
                                   @NonNull RecyclerView.LayoutManager layout,
                                   @Nullable RecyclerView.ItemDecoration decoration) {
        ViewUtil.setRecycler(adapter, recycler, layout, decoration, null, null, null);
    }

    public static void setRecycler(@NonNull SmartAdapter adapter,
                                   @NonNull RecyclerView recycler,
                                   @NonNull RecyclerView.LayoutManager layout,
                                   @Nullable RecyclerView.ItemDecoration decoration,
                                   @Nullable RecyclerView.ItemAnimator animator,
                                   @Nullable RecyclerView.OnScrollListener scroller,
                                   @Nullable View empty) {

        layout.setItemPrefetchEnabled(false);
        recycler.setHasFixedSize(true);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(layout);

        if (decoration != null && recycler.getItemDecorationCount() == 0) {
            recycler.addItemDecoration(decoration);
        }

        if (animator != null) {
            recycler.setItemAnimator(animator);
        } else {
            ((DefaultItemAnimator) Objects.requireNonNull(recycler.getItemAnimator())).setSupportsChangeAnimations(false);
            //recycler.setItemAnimator(null);
        }

        recycler.clearOnScrollListeners();
        if (scroller != null) {
            recycler.addOnScrollListener(scroller);
        }

        if (empty != null) {
            EmptyViewHelper.create(adapter, empty);
        }
    }

    @NonNull
    public static void showSnackbar(@NonNull View view, @StringRes int textId) {
        Snackbar.make(view, textId, Snackbar.LENGTH_LONG).show();
    }

    @NonNull
    public static void showSnackbar(@NonNull View view, String text) {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG).show();
    }

    public static void blink(TextView view, int startColorId, int endColorId) {
        int startColor = ColorUtil.getColor(view.getContext(), startColorId);
        int endColor = ColorUtil.getColor(view.getContext(), endColorId);
        ObjectAnimator animator = ObjectAnimator.ofInt(view, "textColor", startColor, endColor, startColor);
        animator.setDuration(1500);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.RESTART);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ViewUtil.setTextColor(view, endColor);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();

    }

    public static String getText(Context context, @StringRes int keyResId, int value) {
        return String.format(
                context.getString(keyResId),
                value);
    }

    public static String getText(View view) {
        if (TextView.class.isInstance(view)) {
            TextView textView = (TextView) view;
            return textView.getText().toString().trim();
        }
        return null;
    }

    public static void setIcon(Menu menu, int menuItemId, int iconRes) {
        if (menu != null) {
            MenuItem item = menu.findItem(menuItemId);
            if (item != null) {
                item.setIcon(iconRes);
            }
        }
    }

/*    mPopupWindow = new PopupWindow();
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        mPopupWindow.setContentView(popupView);
        if (android.os.Build.VERSION.SDK_INT >=24) {
        int[] a = new int[2];
        anchorView.getLocationInWindow(a);
        mPopupWindow.showAtLocation(getWindow().getDecorView(), Gravity.NO_GRAVITY, 0 , a[1]+anchorView.getHeight());
    } else{
        mPopupWindow.showAsDropDown(anchorView);
    }
        if (Build.VERSION.SDK_INT != 24) {
        mPopupWindow.update(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    } else  {
        mPopupWindow.update();
    }*/
}
