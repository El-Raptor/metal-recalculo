package br.com.sankhya;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.com.sankhya.dao.ItemDAO;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
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
import br.com.sankhya.ws.ServiceContext;

public class Recalculo implements EventoProgramavelJava {

	@Override
	public void afterUpdate(PersistenceEvent ctx) throws Exception {
		SessionHandle hnd = null;

		try {
			hnd = JapeSession.open();

			// Obtém as instâncias atuais da tabela TGFCAB
			DynamicVO cabVO = (DynamicVO) ctx.getVo();

			BigDecimal nunota = cabVO.asBigDecimal("NUNOTA");

			// Se a nota não for nem Pedido nem Venda.
			if (!cabVO.asString("TIPMOV").equals("P") && !cabVO.asString("TIPMOV").equals("V"))
				// throw new Exception("Movimentação tem q ser igual a P ou V");
				return;

			JapeWrapper itemDAO = JapeFactory.dao(DynamicEntityNames.ITEM_NOTA);
			Collection<DynamicVO> itensVO = (Collection<DynamicVO>) itemDAO.find("NUNOTA = ?", nunota);

			// Se for o primeiro produto a inserir.
			if (itensVO.size() <= 1)
				return;

			boolean precisaCalcular = false;
			// Verifica se há algum item que não foi recalculado.
			for (DynamicVO itemVO : itensVO) {
				if (ItemDAO.coalesce(itemVO, "AD_RECALCULADO").equals("N")) {
					precisaCalcular = true;
					break;
				}
			}

			// Se o peso da nota não mudou o programa para por aqui.
			if (precisaCalcular) {
				for (DynamicVO itemVO : itensVO) {
					Item item = Item.builder(itemVO); // Inicializando item.
					item = ItemDAO.calcVlrUnit(hnd, item); // Calcula o preço do produto.
					updateItemOrder(item, itemVO); // Atualiza o item da nota no sistema.
					System.out.println("Sequencia: " + item.getSequencia() + "\nVlr Unit: " + item.getVlrunit());
				}
			}

			// if (hnd != null)
			// throw new Exception("Nunota: " + nunota + "\nPeso: " +
			// cabVO.asBigDecimal("PESOBRUTO")
			// + "\nPeso Antigo: " + oldPeso);

		} catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
			MGEModelException.throwMe(e);
		} finally {
			JapeSession.close(hnd);
		}

	}

	/**
	 * Este método atualiza o item da nota passado.
	 * 
	 * @param item   instância de um item com as propriedades que serão usadas para
	 *               alterar o item no sistema.
	 * @param itemVO instância de um registro do item de uma nota que será alterado.
	 * @throws Exception
	 */
	private static void updateItemOrder(Item item, DynamicVO itemVO) throws Exception {
		// Variáveis do sistema nos quais permitem recalcular o financeiro
		JapeSessionContext.putProperty("br.com.sankhya.com.CentralCompraVenda", Boolean.TRUE);
		JapeSessionContext.putProperty("ItemNota.incluindo.alterando.pela.central", Boolean.TRUE);

		ServiceContext service = ServiceContext.getCurrent();

		BigDecimal vlrunit = item.getVlrunit();

		// itemVO.setProperty("CODPROD", item.getCodprod());
		itemVO.setProperty("QTDNEG", item.getQtdneg());
		// itemVO.setProperty("VLRDESC", item.getVlrdesc());
		// itemVO.setProperty("PERCDESC", item.getPercdesc());
		itemVO.setProperty("VLRUNIT", item.getVlrunit());
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
