package life.haq.it.android.snapchatlikecamera.camera;

import android.view.MotionEvent;

public class DeleteIconEvent implements StickerIconEvent {
  @Override
  public void onActionDown(StickerView stickerView, MotionEvent event) {

  }

  @Override
  public void onActionMove(StickerView stickerView, MotionEvent event) {

  }

  @Override
  public void onActionUp(StickerView stickerView, MotionEvent event) {
    stickerView.removeCurrentSticker();
  }
}
