package fr.calamus.common.mail.core;

import java.util.List;

import fr.calamus.common.mail.model.ListeDestinataires;

public interface IControleurDestinataires {

	ListeDestinataires getListeDestinatairesParIds(List<Integer> ids);

}
