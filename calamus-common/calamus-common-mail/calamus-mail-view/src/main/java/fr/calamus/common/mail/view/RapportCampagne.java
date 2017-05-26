package fr.calamus.common.mail.view;

import fr.calamus.common.mail.core.IReceveurEventsEnvoiMails;
import fr.calamus.common.mail.model.EtatsLivraisonMessage;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Vector;
import javax.mail.event.TransportEvent;
import javax.swing.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXTextArea;

public class RapportCampagne extends JPanel implements IReceveurEventsEnvoiMails {
	private static final long serialVersionUID = 7206852102235635934L;
	private static final Log log=LogFactory.getLog(RapportCampagne.class);
	protected JXTextArea textArea;
	private List<JLabel>labelsReception;
	private List<JTextField>champsReception;

	public RapportCampagne(){
		super(new BorderLayout(3,3));
		textArea=new JXTextArea();
		labelsReception=new Vector<JLabel>();
		champsReception=new Vector<JTextField>();
		int n=EtatsLivraisonMessage.values().length;
		JPanel p=new JPanel(new GridLayout(n,2));
		for(int i=0; i<n; i++){
			labelsReception.add(new JLabel(EtatsLivraisonMessage.values()[i].getLabel()));
			champsReception.add(new JTextField("0"));
			p.add(labelsReception.get(i));
			p.add(champsReception.get(i));
		}
		add(p, BorderLayout.NORTH);
		add(textArea, BorderLayout.CENTER);
	}

	@Override
	public void ajouterLigne(final String ligne){
		SwingUtilities.invokeLater(new Runnable(){
			
			@Override
			public void run(){
				textArea.append(ligne+"\n");
				repaint();
			}
		});
	}

//	@Override
	public void finEnvoi(){
		textArea.append("Envoi terminé.\n");
		repaint();
	}

	@Override
	public void addTransportEvent(EtatsLivraisonMessage type, TransportEvent e){
		log.debug(" addTransportEvent "+type+" "+e);
		incrementer(type);
	}

	private void incrementer(EtatsLivraisonMessage type){
		int nType=type.ordinal();
		int cpt=Integer.parseInt(champsReception.get(nType).getText());
		cpt++;
		champsReception.get(nType).setText(""+cpt);
		repaint();
	}

	@Override
	public void success() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void failed(String cause) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
