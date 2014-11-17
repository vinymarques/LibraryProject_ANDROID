package br.com.rubythree.library.models;

import android.os.Handler;

public class ModelHandler extends Handler {
	protected Base object;
	
	public ModelHandler(Base object) {
		this.object = object;
	}
}
