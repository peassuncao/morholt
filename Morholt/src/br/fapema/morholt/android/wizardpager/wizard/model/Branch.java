package br.fapema.morholt.android.wizardpager.wizard.model;

import java.io.Serializable;

public class Branch implements Serializable{
	private static final long serialVersionUID = 2092993092660718478L;
	public String choice;
    public PageList childPageList;

    public Branch(String choice, Page... pages) {
        this.choice = choice;
        this.childPageList = new PageList(pages);
    }
    
    public Branch(String choice, PageList childPageList) {
        this.choice = choice;
        this.childPageList = childPageList;
    }
}
