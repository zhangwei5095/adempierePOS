/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 Adempiere, Inc. All Rights Reserved.               *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/

package org.compiere.pos;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import net.miginfocom.swing.MigLayout;

import org.compiere.minigrid.ColumnInfo;
import org.compiere.minigrid.IDColumn;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.MWarehousePrice;
import org.compiere.model.PO;
import org.compiere.swing.CScrollPane;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 * Current Line Sub Panel
 * 
 * @author OpenXpertya
 * Based on Modified Original Code, Revised and Optimized
 *         *Copyright Jorg Janke
 * red1 - [2093355 ] Small bugs in OpenXpertya POS
 *  @author Susanne Calderón Schöningh, Systemhaus Westfalia
 *  @author Yamel Senih, ysenih@erpcya.com, ERPCyA http://www.erpcya.com
 *  <li> Implement best practices
 *  
 *  @version $Id: QueryProduct.java,v 1.1 jjanke Exp $
 *  @version $Id: QueryProduct.java,v 2.0 2015/09/01 00:00:00 scalderon
 *  
 */
public class POSOrderLinePanel extends PosSubPanel 
	implements ActionListener, FocusListener, 
		ListSelectionListener,  TableModelListener, I_POSPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4023538043556457231L;

	/**
	 * Constructor
	 * 
	 * @param posPanel POS Panel
	 */
	public POSOrderLinePanel(VPOS posPanel) {
		super(posPanel);
	}

	/**	Label Order			*/
	private BigDecimal	 	f_price;
	private BigDecimal		f_quantity;
	private int 			m_C_OrderLine_ID = 0;
	

	/**	The Product					*/
	private MProduct		m_product = null; 

	/** Warehouse					*/
	private int 			m_M_Warehouse_ID;
	/** PLV							*/
	private int 			m_M_PriceList_Version_ID;
	
	
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(POSOrderLinePanel.class);
	

	/** The Table					*/
	private PosTable		m_table;
	/** The Query SQL				*/
	private String			m_sql;
	/**	Logger			*/
	
	static final private String NAME        = "Name";
	static final private String QTY         = "Qty";
	static final private String C_UOM_ID    = "C_UOM_ID";
	static final private String PRICEACTUAL = "PriceActual";
	static final private String LINENETAMT  = "LineNetAmt";
	static final private String GRANDTOTAL  = "GrandTotal";
	
	/**	Table Column Layout Info			*/
	private static ColumnInfo[] s_layout = new ColumnInfo[] 
	{
		new ColumnInfo(" ", "C_OrderLine_ID", IDColumn.class), 
		new ColumnInfo(Msg.translate(Env.getCtx(), NAME), "p_Name", String.class),
		new ColumnInfo(Msg.translate(Env.getCtx(), QTY), "QtyOrdered", Double.class,false,true,null),
		new ColumnInfo(Msg.translate(Env.getCtx(), C_UOM_ID), "UOM_name", String.class),
		new ColumnInfo(Msg.translate(Env.getCtx(), PRICEACTUAL), PRICEACTUAL, BigDecimal.class,false,true,null), 
		new ColumnInfo(Msg.translate(Env.getCtx(), LINENETAMT), LINENETAMT, BigDecimal.class), 
		new ColumnInfo(Msg.translate(Env.getCtx(), "C_Tax_ID"), "TaxIndicator", String.class, true, true, null), 
		new ColumnInfo(Msg.translate(Env.getCtx(), GRANDTOTAL), GRANDTOTAL, BigDecimal.class,  true, true, null), 
	};
	/**	From Clause							*/
	private static String s_sqlFrom = "POS_OrderLine_v";
	/** Where Clause						*/
	private static String s_sqlWhere = "C_Order_ID=? AND LineNetAmt <> 0"; 
	
	/**
	 * Initialize
	 */ 
	public void init() {
	
		//	Content
		setLayout(new MigLayout("fill, ins 10 10"));
	
		m_table = new PosTable();
		CScrollPane scroll = new CScrollPane(m_table);
		m_sql = m_table.prepareTable (s_layout, s_sqlFrom, 
				s_sqlWhere, false, "POS_OrderLine_v");
//		m_table.getSelectionModel().addListSelectionListener(this);
		m_table.setColumnVisibility(m_table.getColumn(0), false);
		m_table.getColumn(1).setPreferredWidth(320);
		m_table.getColumn(2).setPreferredWidth(65);
		m_table.getColumn(3).setPreferredWidth(30);
		m_table.getColumn(4).setPreferredWidth(75);
		m_table.getColumn(5).setPreferredWidth(75);
		m_table.getColumn(6).setPreferredWidth(70);
		m_table.getColumn(7).setPreferredWidth(75);
		m_table.setColumnClass(7, BigDecimal.class, false);
		m_table.setFocusable(false);
		m_table.getColumnModel().getColumn(7).setCellEditor(
				new BigDecimalEditor());
		m_table.getModel().addTableModelListener(this);
		m_table.addKeyListener(new java.awt.event.KeyAdapter() {
			
			@Override
			public void keyTyped(KeyEvent e) {// GEN-FIRST:
				// event_DetailKeyPressed
				switch (e.getKeyCode()) {
				case KeyEvent.VK_ALT:
					break;
				case KeyEvent.VK_A:
					break;
				default:
					break;
				}
				v_POSPanel.updateInfo();
			}
			
			@Override
			public void keyReleased(KeyEvent e) {// GEN-FIRST:
				// event_DetailKeyPressed
				switch (e.getKeyCode()) {
				case KeyEvent.VK_ALT:
					break;
				case KeyEvent.VK_A:
					break;

				default:
					break;
				}

				v_POSPanel.updateInfo();
			}
			
			@Override
			public void keyPressed(KeyEvent e) {// GEN-FIRST:
				// event_DetailKeyPressed
				switch (e.getKeyCode()) {
				case KeyEvent.VK_ALT:
					break;
				case KeyEvent.VK_A:
					break;

				default:
					break;
				}
				v_POSPanel.updateInfo();

			}

    private int[] getSelectedCell() {
         int[] xy = new int[2];
         xy[0] = m_table.getSelectedRow();
         xy[1] = m_table.getSelectedColumn();
         return xy;
    }

			
		});

		m_table.setFillsViewportHeight( true ); //@Trifon
		m_table.growScrollbars();
		add (scroll, "growx, spanx, growy, pushy, h 100:30:");
		
		setQty(Env.ZERO);
		
		setPrice(Env.ZERO);
		
	} //init


	/**
	 * Dispose - Free Resources
	 */
	public void dispose() {
		super.dispose();
	} //	dispose

	/**
	 * Action Listener
	 * 
	 * @param e event
	 */
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if (action == null || action.length() == 0)
			return;
		log.info( "SubCurrentLine - actionPerformed: " + action);
		
		//	Plus
		if (action.equals("Plus"))
		{
			if ( m_C_OrderLine_ID > 0 )
			{
				MOrderLine line = new MOrderLine(p_ctx, m_C_OrderLine_ID, null);
				if ( line != null )
				{
					line.setQty(line.getQtyOrdered().add(Env.ONE));
					line.saveEx();
					v_POSPanel.updateInfo();
				}
			}

		}
		//	Minus
		else if (action.equals("Minus"))
		{
			if ( m_C_OrderLine_ID > 0 )
			{
				MOrderLine line = new MOrderLine(p_ctx, m_C_OrderLine_ID, null);
				if ( line != null )
				{
					line.setQty(line.getQtyOrdered().subtract(Env.ONE));
					line.saveEx();
					v_POSPanel.updateInfo();
				}
			}

		}
		//	Product
		if (action.equals("Product"))
		{
			setParameter();
//			QueryProduct qt = new QueryProduct(p_posPanel);
//			qt.setQueryData(m_M_PriceList_Version_ID, m_M_Warehouse_ID);
//			qt.setVisible(true);
			refreshPanel();
			int row = m_table.getSelectedRow();
			if (row < 0) row = 0;
			m_table.getSelectionModel().setSelectionInterval(row, row);
			// https://sourceforge.net/tracker/?func=detail&atid=879332&aid=3121975&group_id=176962
			m_table.scrollRectToVisible(m_table.getCellRect(row, 1, true)); //@Trifon - BF[3121975]
		}
		if ("Previous".equalsIgnoreCase(e.getActionCommand()))
		{
			int rows = m_table.getRowCount();
			if (rows == 0) 
				return;
			int row = m_table.getSelectedRow();
			row--;
			if (row < 0)
				row = 0;
			m_table.getSelectionModel().setSelectionInterval(row, row);
			// https://sourceforge.net/tracker/?func=detail&atid=879332&aid=3121975&group_id=176962
			m_table.scrollRectToVisible(m_table.getCellRect(row, 1, true)); //@Trifon - BF[3121975]
			return;
		}
		else if ("Next".equalsIgnoreCase(e.getActionCommand()))
		{
			int rows = m_table.getRowCount();
			if (rows == 0)
				return;
			int row = m_table.getSelectedRow();
			row++;
			if (row >= rows)
				row = rows - 1;
			m_table.getSelectionModel().setSelectionInterval(row, row);
			// https://sourceforge.net/tracker/?func=detail&atid=879332&aid=3121975&group_id=176962
			m_table.scrollRectToVisible(m_table.getCellRect(row, 1, true)); //@Trifon - BF[3121975]
			return;
		}
		//	Delete
		else if (action.equals("Cancel"))
		{
			int rows = m_table.getRowCount();
			if (rows != 0)
			{
				int row = m_table.getSelectedRow();
				if (row != -1)
				{
					if (v_POSPanel.getM_Order() != null)
						v_POSPanel.deleteLine(m_table.getSelectedRowKey());
					setQty(null);
					setPrice(null);
					
					m_C_OrderLine_ID = 0;
				}
			}
		}
		v_POSPanel.updateInfo();
	} //	actionPerformed
	
	/**
	 * 	Set Query Parameter
	 */
	private void setParameter()
	{
		//	What PriceList ?
		m_M_Warehouse_ID = p_pos.getM_Warehouse_ID();
		m_M_PriceList_Version_ID = v_POSPanel.getM_PriceList_Version_ID();
	}	//	setParameter

	/**
	 * Set Price
	 * 
	 * @param price
	 */
	public void setPrice(BigDecimal price) {
		if (price == null)
			price = Env.ZERO;
		f_price = price;
	} //	setPrice

	/**
	 * Get Price
	 * 
	 * @return price
	 */
	public BigDecimal getPrice() {
		return f_price;
	} //	getPrice

	/**
	 * Set Qty
	 * 
	 * @param qty
	 */
	public void setQty(BigDecimal qty) {
		f_quantity = qty;
	} //	setQty

	/**
	 * Get Qty
	 * 
	 * @return qty
	 */
	public BigDecimal getQty() {
		return f_quantity;
	} //	getQty

	/***************************************************************************
	 * New Line
	 */
	public void newLine() {
		setM_Product_ID(0);
		setQty(Env.ONE);
		setPrice(Env.ZERO);
		m_C_OrderLine_ID = 0;
	} //	newLine

	/**
	 * Save Line
	 * 
	 * @return true if saved
	 */
	public boolean saveLine() {
		MProduct product = getProduct();
		if (product == null)
			return false;
		BigDecimal QtyOrdered = f_quantity;
		BigDecimal PriceActual = f_price;
		
		if (v_POSPanel.getM_Order() == null) {
			v_POSPanel.newOrder();
		}
		
		MOrderLine line = null;
		
		if (v_POSPanel.getM_Order() != null) {
			line = v_POSPanel.createLine(product, QtyOrdered, PriceActual);

			if (line == null)
				return false;
			if (!line.save())
				return false;
		}
		
		m_C_OrderLine_ID = line.getC_OrderLine_ID();
		setM_Product_ID(0);
		//
		return true;
	} //	saveLine


	/**
	 * 	Get Product
	 *	@return product
	 */
	public MProduct getProduct()
	{
		return m_product;
	}	//	getProduct
	
	/**
	 * 	Set Price for defined product 
	 */
	public void setPrice()
	{
		if (m_product == null)
			return;
		//
		setParameter();
		MWarehousePrice result = MWarehousePrice.get (m_product,
			m_M_PriceList_Version_ID, m_M_Warehouse_ID, null);
		if (result != null)
			v_POSPanel.f_curLine.setPrice(result.getPriceStd());
		else
			v_POSPanel.f_curLine.setPrice(Env.ZERO);

	}	//	setPrice

	/**************************************************************************
	 * 	Set Product
	 *	@param M_Product_ID id
	 */
	public void setM_Product_ID (int M_Product_ID)
	{
		log.fine( "PosSubProduct.setM_Product_ID=" + M_Product_ID);
		if (M_Product_ID <= 0)
			m_product = null;
		else
		{
			m_product = MProduct.get(p_ctx, M_Product_ID);
			if (m_product.get_ID() == 0)
				m_product = null;
		}
		//	Set String Info
		if (m_product != null)
		{
//			f_name.setText(m_product.getName());
//			f_name.setToolTipText(m_product.getDescription());
		}
		else
		{
//			f_name.setText(null);
//			f_name.setToolTipText(null);
		}
	}	//	setM_Product_ID
	
	/**
	 * 	Focus Gained
	 *	@param e
	 */
	public void focusGained (FocusEvent e)
	{
		
		
	}	//	focusGained
		

	/**
	 * 	Focus Lost
	 *	@param e
	 */
	public void focusLost (FocusEvent e)
	{
		if (e.isTemporary())
			return;
		log.info( "PosSubProduct - focusLost");
//		findProduct();
		v_POSPanel.updateInfo();
	}	//	focusLost


	public void valueChanged(ListSelectionEvent e) {
		if ( e.getValueIsAdjusting() )
			return;

		int row = m_table.getSelectedRow();
		if (row != -1 ) {
			Object data = m_table.getModel().getValueAt(row, 0);
			if ( data != null )	{
				Integer id = (Integer) ((IDColumn)data).getRecord_ID();
				m_C_OrderLine_ID = id;
				loadLine(id);
			}
		}

		v_POSPanel.refreshHeader();
		
	}  // valueChanged

	private void loadLine(int lineId) {
		
		if ( lineId <= 0 )
			return;
	
		log.fine("SubCurrentLine - loading line " + lineId);
		MOrderLine ol = new MOrderLine(p_ctx, lineId, null);
		if ( ol != null ) {
			setPrice(ol.getPriceActual());
			setQty(ol.getQtyOrdered());
		}
	}
    public void tableChanged(TableModelEvent e) {
        int column = e.getColumn();
		int id = m_table.getSelectedRow();
		boolean isUpdate = (e.getType() == TableModelEvent.UPDATE);
		if (!isUpdate) {
			return;
		}
        if (id != -1) {	
    		IDColumn key = (IDColumn) m_table.getModel().getValueAt(id, 0);
    		
    		if ( key != null &&  key.getRecord_ID() != m_C_OrderLine_ID )
    			m_C_OrderLine_ID = key.getRecord_ID();
			
    		BigDecimal price = new BigDecimal(m_table.getModel().getValueAt(id, 4).toString());
			BigDecimal qty = new BigDecimal(m_table.getModel().getValueAt(id, 2).toString());
			m_table.getModel().removeTableModelListener(this);
			
    			MOrderLine line = new MOrderLine(p_ctx, m_C_OrderLine_ID, null);
    			if ( line != null ) {
    					line.setPrice(price);
    					line.setQty(qty);
    					line.saveEx();
    					BigDecimal grandTotal = line.getLineNetAmt();
    					m_table.getModel().setValueAt(line.getLineNetAmt(), id, 5);
    					if(!line.getC_Tax().getRate().equals(null)){
    						grandTotal.multiply(line.getC_Tax().getRate());
    					} 
    					m_table.getModel().setValueAt(grandTotal, id, 7);
    					if(qty.compareTo(Env.ZERO) <= 0){
//    						p_posPanel.updateInfo();
    					}
    			}
    			v_POSPanel.reload();
    			v_POSPanel.refreshPanel();
    			m_table.getModel().addTableModelListener(this);
    			
    	}
       // ...// Do something with the data...
    }

	@Override
	public void refreshPanel() {
		if (!v_POSPanel.hasOrder()) {
			v_POSPanel.f_curLine.m_table.loadTable(new PO[0]);
			v_POSPanel.refreshHeader();
		}
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = DB.prepareStatement (m_sql, null);
			pstmt.setInt (1, v_POSPanel.getC_Order_ID());
			rs = pstmt.executeQuery ();
			m_table.loadTable(rs);
		} catch (Exception e) {
			log.log(Level.SEVERE, m_sql, e);
		} finally {
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		
		for ( int i = 0; i < m_table.getRowCount(); i ++ ) {
			IDColumn key = (IDColumn) m_table.getModel().getValueAt(i, 0);
			if ( key != null && m_C_OrderLine_ID > 0 && key.getRecord_ID() == m_C_OrderLine_ID ) {
				m_table.getSelectionModel().setSelectionInterval(i, i);
				break;
			}
		}
		//	Refresh
		v_POSPanel.refreshHeader();
	}


	@Override
	public String validatePanel() {
		return null;
	}


	@Override
	public void changeViewPanel() {
		
	}
} //	PosSubCurrentLine