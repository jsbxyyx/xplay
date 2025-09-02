package com.github.jsbxyyx.xbook;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.jsbxyyx.xbook.common.Common;

public class DialogConfirm extends Dialog {

    public DialogConfirm(Context context,
                         String title,
                         String content,
                         String extraText,
                         OnConfirmListener listener) {
        this(context, title, content, extraText, "OK", "CANCEL", listener);
    }

    public DialogConfirm(Context context,
                         String title,
                         String content,
                         String extraText,
                         String okText,
                         String cancelText,
                         OnConfirmListener listener) {
        super(context);
        setContentView(R.layout.dialog_confirm_with_checkbox);

        Window window = getWindow();
        if (window != null) {
            window.setLayout((int) (context.getResources().getDisplayMetrics().widthPixels * 0.8), WindowManager.LayoutParams.WRAP_CONTENT);
        }

        TextView tvTitle = findViewById(R.id.tv_dialog_title);
        TextView tvContent = findViewById(R.id.tv_dialog_content);
        CheckBox cbExtra = findViewById(R.id.cb_extra);
        TextView tvExtra = findViewById(R.id.tv_extra);
        LinearLayout layoutCheck = (LinearLayout) cbExtra.getParent();
        Button btnOk = findViewById(R.id.btn_ok);
        Button btnCancel = findViewById(R.id.btn_cancel);

        tvTitle.setText(title);
        tvContent.setText(content);
        btnOk.setText(okText);
        btnCancel.setText(cancelText);

        if (Common.isBlank(extraText)) {
            layoutCheck.setVisibility(View.GONE);
        } else {
            tvExtra.setText(extraText);
            layoutCheck.setVisibility(View.VISIBLE);
        }

        btnCancel.setOnClickListener(v -> {
            dismiss();
            if (listener != null) {
                listener.onCancel();
            }
        });

        btnOk.setOnClickListener(v -> {
            boolean checked = cbExtra.isChecked();
            dismiss();
            if (listener != null) {
                listener.onConfirm(checked);
            }
        });
    }

    public interface OnConfirmListener {
        void onConfirm(boolean extraChecked);

        void onCancel();
    }

}
