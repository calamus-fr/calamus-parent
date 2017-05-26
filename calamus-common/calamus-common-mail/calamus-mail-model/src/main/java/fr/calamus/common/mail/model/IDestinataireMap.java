/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.calamus.common.mail.model;

/**
 *
 * @author haerwynn
 */
public interface IDestinataireMap {

	public Integer getId();

	public void setId(Integer id);

	public String getNom();

	public void setNom(String nom);

	public String getMail();

	public void setMail(String mail);

	public String getValeurAffichable(String key);
}
