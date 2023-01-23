package br.com.sankhya;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.com.sankhya.dao.ItemDAO;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.util.JapeSessionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.model.Item;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.comercial.CentralItemNota;
import br.com.sankhya.modelcore.comercial.centrais.CACHelper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.ws.ServiceContext;

public class Recalculo implements EventoProgramavelJava {

	@Override
	public void afterUpdate(PersistenceEvent ctx) throws Exception {
		SessionHandle hnd = null;
		JdbcWrapper jdbc = null;

		try {
			hnd = JapeSession.open();
			EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
			jdbc = dwfEntityFacade.getJdbcWrapper();

			DynamicVO iteVO = (DynamicVO) ctx.getVo();
			BigDecimal nunota = iteVO.asBigDecimal("NUNOTA");
			BigDecimal sequencia = iteVO.asBigDecimal("SEQUENCIA");

			// Se for a primeira sequ�ncia.
			if (sequencia.equals(BigDecimal.ONE))
				return;

			JapeWrapper itemDAO = JapeFactory.dao(DynamicEntityNames.ITEM_NOTA);
			Collection<DynamicVO> itensVO = (Collection<DynamicVO>) itemDAO.find("NUNOTA = ?", nunota);

			for (DynamicVO itemVO : itensVO) {
				// Inicializando item
				Item item = Item.builder(itemVO);
				ItemDAO.update(jdbc, item);
				updateItemOrder(item, itemVO);
			}

			// if (hnd != null)
			// throw new Exception("Nunota: " + nunota + "\nPeso: " + peso + "\nQtdNeg: " +
			// qtdneg);

		} catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
			MGEModelException.throwMe(e);
		} finally {
			JapeSession.close(hnd);
			jdbc.closeSession();
		}

	}

	/**
	 * Este m�todo atualiza o item da nota passado.
	 * 
	 * @param item   inst�ncia de um item com as propriedades que ser�o usadas para
	 *               alterar o item no sistema.
	 * @param itemVO inst�ncia de um registro do item de uma nota que ser� alterado.
	 * @throws Exception
	 */
	private static void updateItemOrder(Item item, DynamicVO itemVO) throws Exception {
		// Vari�veis do sistema nos quais permitem recalcular o financeiro
		JapeSessionContext.putProperty("br.com.sankhya.com.CentralCompraVenda", Boolean.TRUE);
		JapeSessionContext.putProperty("ItemNota.incluindo.alterando.pela.central", Boolean.TRUE);

		ServiceContext service = ServiceContext.getCurrent();

		BigDecimal vlrunit = item.getVlrunit();

		// itemVO.setProperty("CODPROD", item.getCodprod());
		// itemVO.setProperty("QTDNEG", item.getQtdneg());
		// itemVO.setProperty("VLRDESC", item.getVlrdesc());
		// itemVO.setProperty("PERCDESC", item.getPercdesc());
		itemVO.setProperty("VLRUNIT", new BigDecimal(23));
		itemVO.setProperty("VLRTOT", vlrunit.multiply(item.getQtdneg()));

		CentralItemNota itemNota = new CentralItemNota();
		itemNota.recalcularValores("VLRUNIT", vlrunit.toString(), itemVO, itemVO.asBigDecimal("NUNOTA"));

		List<DynamicVO> itensFatura = new ArrayList<DynamicVO>();
		itensFatura.add(itemVO);

		CACHelper cacHelper = new CACHelper();
		cacHelper.incluirAlterarItem(itemVO.asBigDecimal("NUNOTA"), service, null, false, itensFatura);
	}

	@Override
	public void afterInsert(PersistenceEvent ctx) throws Exception {
	}

	@Override
	public void beforeDelete(PersistenceEvent ctx) throws Exception {
	}

	@Override
	public void afterDelete(PersistenceEvent ctx) throws Exception {
	}

	@Override
	public void beforeCommit(TransactionContext ctx) throws Exception {
	}

	@Override
	public void beforeInsert(PersistenceEvent ctx) throws Exception {
	}

	@Override
	public void beforeUpdate(PersistenceEvent ctx) throws Exception {

	}
}
