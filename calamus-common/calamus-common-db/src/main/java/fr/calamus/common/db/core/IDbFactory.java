/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.calamus.common.db.core;

/**
 *
 * @author haerwynn
 */
public interface IDbFactory<T extends IDbFactory> {
	public DbAccess dbAccess();
	public T getNewInstance();
}
