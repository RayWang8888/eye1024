package com.eye1024.view;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;


/**
 * 不基于v4包的DialogFragment
 */
public class DialogFragment extends android.app.DialogFragment{

    /**
     * Interface definition for passing style data.
     */
    public interface Builder{
        /**
         * Get a Dialog instance used for this fragment.
         * @param context A Context instance.
         * @return The Dialog will be used for this fragment.
         */
        public com.eye1024.view.Dialog build(Context context);

        /**
         * Handle click event on Positive Action.
         */
        public void onPositiveActionClicked(DialogFragment fragment);

        /**
         * Handle click event on Negative Action.
         */
        public void onNegativeActionClicked(DialogFragment fragment);

        /**
         * Handle click event on Neutral Action.
         */
        public void onNeutralActionClicked(DialogFragment fragment);
    }

    protected static final String ARG_BUILDER = "arg_builder";

    protected Builder mBuilder;

    private Dialog mDialog;

    private View.OnClickListener mActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mBuilder == null)
                return;

            if(v.getId() == Dialog.ACTION_POSITIVE)
                mBuilder.onPositiveActionClicked(DialogFragment.this);
            else if(v.getId() == Dialog.ACTION_NEGATIVE)
                mBuilder.onNegativeActionClicked(DialogFragment.this);
            else if(v.getId() == Dialog.ACTION_NEUTRAL)
                mBuilder.onNeutralActionClicked(DialogFragment.this);
        }
    };

    public static DialogFragment newInstance(Builder builder){
        DialogFragment fragment = new DialogFragment();
        fragment.mBuilder = builder;
        return fragment;
    }

    public static DialogFragment newInstance(Dialog mDialog){
        DialogFragment fragment = new DialogFragment();
        fragment.mDialog = mDialog;
        return fragment;
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {

        return super.show(transaction, tag);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(mDialog == null) {
            mDialog = mBuilder == null ? new Dialog(getActivity()) : mBuilder.build(getActivity());
        }
        mDialog.positiveActionClickListener(mActionListener)
                .negativeActionClickListener(mActionListener)
                .neutralActionClickListener(mActionListener);
        return mDialog;
    }

    public Dialog getmDialog(){
        return mDialog;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null && mBuilder == null)
            mBuilder = (Builder)savedInstanceState.getParcelable(ARG_BUILDER);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(mBuilder != null && mBuilder instanceof Parcelable)
            outState.putParcelable(ARG_BUILDER, (Parcelable)mBuilder);
    }

}
