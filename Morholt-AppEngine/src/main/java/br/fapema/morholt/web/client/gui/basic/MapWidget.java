package br.fapema.morholt.web.client.gui.basic;

import com.google.gwt.maps.client.MapOptions;
import com.google.gwt.maps.client.events.resize.ResizeMapEvent;
import com.google.gwt.maps.client.events.resize.ResizeMapHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

public class MapWidget extends com.google.gwt.maps.client.MapWidget {

protected String width;
protected String heigth;

/**
 * create a new Map Widget
 * @param options {@link MapOptions}
 */
public MapWidget(MapOptions options) {
    super(options);
    this.addResizeHandler(new ResizeMapHandler() {

        @Override
        public void onEvent(ResizeMapEvent event) {
            if (MapWidget.this.width != null && MapWidget.this.heigth != null) {
                setSize(MapWidget.this.width, MapWidget.this.heigth);
            }
        }
    });
}
@Override
protected void onAttach() {
    super.onAttach();
    Timer timer = new Timer() {

        @Override
        public void run() {
            resize();
        }
    };
    timer.schedule(5);
}

/*
 * (non-Javadoc)
 * 
 * @see com.google.gwt.user.client.ui.UIObject#setSize(java.lang.String, java.lang.String)
 */
@Override
public void setSize(String width, String height) {
    super.setSize(width, height);
    this.width = width;
    this.heigth = height;

}

public void resize() {
    ((MapImpl) impl).reSize();
}

}
