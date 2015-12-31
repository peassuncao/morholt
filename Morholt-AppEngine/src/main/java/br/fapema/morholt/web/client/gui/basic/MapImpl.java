package br.fapema.morholt.web.client.gui.basic;

public class MapImpl extends com.google.gwt.maps.client.MapImpl {

    protected MapImpl() {}

/**
 * private native method
 * @param id - dom element id
 * @param {@link MapOptions}
 */
    final native MapImpl reSize() /*-{
    return new $wnd.google.maps.event.trigger(this, 'resize');
}-*/;

}
