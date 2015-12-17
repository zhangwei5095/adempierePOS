/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * Copyright (C) 2003-2014 E.R.P. Consultores y Asociados, C.A.               *
 * All Rights Reserved.                                                       *
 * Contributor(s): Raul Muñoz www.erpcya.com					              *
 *****************************************************************************/
package org.adempiere.pos;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;

import javax.swing.KeyStroke;

import org.adempiere.pos.service.I_POSPanel;
import org.compiere.grid.ed.VNumber;
import org.compiere.swing.CButton;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 * Button panel Up Down
 * @author Mario Calderon, mario.calderon@westfalia-it.com, Systemhaus Westfalia, http://www.westfalia-it.com
 * @author Raul Muñoz, rmunoz@erpcya.com, ERPCYA http://www.erpcya.com
 * @author Yamel Senih, ysenih@erpcya.com, ERPCyA http://www.erpcya.com
 * @author Victor Perez , victor.perez@e-evolution.com , e-Evolution http//www.e-evolution.com
 */
public class POSQuantityPanel extends POSSubPanel implements I_POSPanel, ActionListener {

	public POSQuantityPanel(VPOS posPanel) {
		super(posPanel);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8002370020007368736L;
	
	/** Buttons Controls  		*/
	private CButton 			buttonUp;
	private CButton 			buttonDelete;
	private CButton				buttonDown;
	private CButton 			buttonPlus;
	private CButton 			buttonMinus;
	private VNumber 			fieldPrice;
	private VNumber 			fieldQuantity;
	/**	Button Panel			*/
	private CPanel 				buttonPanel;
	/**	Padding					*/
	private int 				topPadding;
	private int 				leftPadding;
	private int 				bottonPadding;
	private int 				rightPadding;

	private final BigDecimal 	currentQuantity =  Env.ONE;
	
	@Override
	protected void init() {
		//	Content
		setLayout(new GridBagLayout());
		//	Button Panel
		buttonPanel = new CPanel(new GridBagLayout());
		//	
		topPadding = 0;
		leftPadding = 1;
		bottonPadding = 0;
		rightPadding = 1;


		buttonDelete = createButtonAction("Cancel", KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		buttonPanel.add(buttonDelete, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(topPadding, leftPadding, bottonPadding, rightPadding), 0, 0));

		buttonPlus = createButtonAction("Plus", KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		buttonPanel.add(buttonPlus, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(topPadding, leftPadding, bottonPadding, rightPadding), 0, 0));

		buttonMinus = createButtonAction("Minus", KeyStroke.getKeyStroke(KeyEvent.VK_MINUS,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		buttonPanel.add(buttonMinus, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(topPadding, leftPadding, bottonPadding, rightPadding), 0, 0));

		buttonUp = createButtonAction("Previous", KeyStroke.getKeyStroke(KeyEvent.VK_UP, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		buttonPanel.add(buttonUp, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(topPadding, leftPadding, bottonPadding, rightPadding), 0, 0));
		buttonDown = createButtonAction("Next", KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		buttonPanel.add(buttonDown, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(topPadding, leftPadding, bottonPadding, rightPadding), 0, 0));

		CLabel qtyLabel = new CLabel(Msg.translate(Env.getCtx(), "QtyOrdered"));
		buttonPanel.add(qtyLabel, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(topPadding, leftPadding, bottonPadding, rightPadding), 0, 0));

		//
		fieldQuantity = new VNumber();
		fieldQuantity.addActionListener(this);
		fieldQuantity.setFont(posPanel.getFont());
		fieldQuantity.setPreferredSize(new Dimension(100, 50));
		fieldQuantity.setMinimumSize(new Dimension(100, 50));
		fieldQuantity.setValue(Env.ZERO);
		buttonPanel.add(fieldQuantity, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(topPadding, leftPadding, bottonPadding, rightPadding), 0, 0));

		CLabel priceLabel = new CLabel(Msg.translate(Env.getCtx(), "PriceActual"));
		buttonPanel.add(priceLabel, new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(topPadding, leftPadding, bottonPadding, rightPadding), 0, 0));

		//
		fieldPrice = new VNumber();
		fieldPrice.setValue(Env.ZERO);
		fieldPrice.addActionListener(this);
		fieldPrice.setFont(posPanel.getFont());
		fieldPrice.setPreferredSize(new Dimension(100, 50));
		fieldPrice.setMinimumSize(new Dimension(100, 50));
		buttonPanel.add(fieldPrice, new GridBagConstraints(8, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(topPadding, leftPadding, bottonPadding, rightPadding), 0, 0));
		
		add(buttonPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1
				,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));

		changeStatus(false);
	}

	/**
	 * 	Distribute actions
	 *	@param e event
	 */
	public void actionPerformed (ActionEvent e) {

		if (e.getSource().equals(buttonUp)){
			posPanel.moveUp();
			return;
		}
		else if (e.getSource().equals(buttonDown)){
			posPanel.moveDown();
			return;
		}
		if (e.getSource().equals(buttonMinus)){
			fieldQuantity.setValue(((BigDecimal) fieldQuantity.getValue()).subtract(currentQuantity));
		}
		else if (e.getSource().equals(buttonPlus)){
			fieldQuantity.setValue(((BigDecimal) fieldQuantity.getValue()).add(currentQuantity));
		}
		else if (e.getSource().equals(buttonDelete)) {
			posPanel.deleteLine(posPanel.getC_OrderLine_ID());
			fieldQuantity.setValue(0.0);
		}

		posPanel.setQuantity((BigDecimal) fieldQuantity.getValue());
		posPanel.setPrice((BigDecimal) fieldPrice.getValue());
		posPanel.updateLineTable();
		posPanel.changeViewQuantityPanel();

	}

	@Override
	public void refreshPanel() {
		if(posPanel.hasLines()){
			buttonDown.setEnabled(true);
			buttonUp.setEnabled(true);
		} else {
			buttonDown.setEnabled(false);
			buttonUp.setEnabled(false);
		}
	}

	@Override
	public void moveUp() {
	}

	@Override
	public void moveDown() {
	}

	@Override
	public String validatePanel() {
		return null;
	}

	/**
	 * Change Status 
	 * @param p_Status
	 */
	public void changeStatus(boolean p_Status) {
		fieldQuantity.setEnabled(p_Status);
		fieldPrice.setEnabled(p_Status);
		buttonDelete.setEnabled(p_Status);
		buttonPlus.setEnabled(p_Status);
		buttonMinus.setEnabled(p_Status);
	}
	
	@Override
	public void changeViewPanel() {
		if(posPanel.getQty().compareTo(Env.ZERO) == 0)
			changeStatus(false);
		else
			changeStatus(true);
		fieldQuantity.setValue(posPanel.getQty().doubleValue());
		fieldPrice.setValue(posPanel.getPrice().doubleValue());
	}
}