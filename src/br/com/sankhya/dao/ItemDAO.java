package br.com.sankhya.dao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.model.Item;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ItemDAO {
	/**
	 * Esse método obtém os dados de uma instância de registro de peça do orçamento
	 * e os insere na instância da classe Item.
	 * 
	 * @param iteVO instância de registro de peça do orçamento;
	 * @return Instância da classe Item.
	 * @throws Exception
	 */
	public static Item read(DynamicVO iteVO) throws Exception {
		Item item = new Item();

		item.setNunota(iteVO.asBigDecimal("NUNOTA"));
		item.setSequencia(iteVO.asBigDecimal("SEQUENCIA"));
		item.setPeso(iteVO.asBigDecimal("PESOLIQ"));
		item.setQtdneg(iteVO.asBigDecimal("QTDNEG"));

		return item;
	}

	/**
	 * 
	 * @param jdbc
	 * @param seqAtual
	 * @param peso
	 * @param nunota
	 */
	public static Item calcVlrUnit(SessionHandle hnd, Item item) throws Exception {

		JdbcWrapper jdbc = null;

		try {
			EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
			jdbc = dwfEntityFacade.getJdbcWrapper();
			NativeSql sql = new NativeSql(jdbc);

			sql.appendSql(" SELECT ");
			sql.appendSql("    SNK_GET_PRECO_ITE_MW( ");
			sql.appendSql("       ITE.NUNOTA, ");
			sql.appendSql("       ITE.SEQUENCIA, ");
			sql.appendSql("       ITE.CODEMP, ");
			sql.appendSql("       :SEQATUAL, ");
			sql.appendSql("       :PESO) ");
			sql.appendSql(" FROM ");
			sql.appendSql("    TGFITE ITE ");
			sql.appendSql(" WHERE ");
			sql.appendSql("    NUNOTA = :NUNOTA ");
			sql.appendSql("    AND SEQUENCIA = :SEQATUAL ");

			sql.setNamedParameter("SEQATUAL", item.getSequencia());
			sql.setNamedParameter("PESO", item.getPeso());
			sql.setNamedParameter("NUNOTA", item.getNunota());

			ResultSet result = sql.executeQuery();

			if (result.next()) {
				item.setVlrunit(result.getBigDecimal(1));
				marcaItem(item.getSequencia(), item.getNunota());
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao calcular o preço do item " + item.getSequencia());
		} finally {
			jdbc.closeSession();
		}

		return item;

	}

	/**
	 * Esse método é responsável por marcar o item que foi calculado.
	 * 
	 * Isso será feito a partir do Update do campo-flag na tabela de itens.
	 * 
	 * @param sequencia
	 * @param nunota
	 * @throws Exception
	 */
	private static void marcaItem(BigDecimal sequencia, BigDecimal nunota) throws Exception {

		try {
			JapeFactory.dao(DynamicEntityNames.ITEM_NOTA)
					.prepareToUpdateByPK(nunota, sequencia)
					.set("AD_RECALCULADO", "S")
					.update();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao marcar o item.");
		}

	}

	/**
	 * Esse retorna o valor de um campo buscado da instância de um registro da peça
	 * ou, caso este seja nulo, retorna 0 (zero).
	 * 
	 * @param iteVO instância de um registro da peça.
	 * @param field campo a ser buscado.
	 * @return retorna o valor do campo buscado ou 0 (zero).
	 */
	public static String coalesce(DynamicVO iteVO, String field) {
		return iteVO.asString(field) == null ? "N" : iteVO.asString(field);
	}
}
