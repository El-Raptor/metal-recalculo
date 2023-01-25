package br.com.sankhya.dao;

import java.math.BigDecimal;
import java.sql.ResultSet;

import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.model.Item;

public class ItemDAO {
	/**
	 * Esse m�todo obt�m os dados de uma inst�ncia de registro de pe�a do or�amento
	 * e os insere na inst�ncia da classe Item.
	 * 
	 * @param iteVO inst�ncia de registro de pe�a do or�amento;
	 * @return Inst�ncia da classe Item.
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
	public static Item calcVlrUnit(JdbcWrapper jdbc, Item item)
			throws Exception {
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

		if (result.next())
			item.setVlrunit(result.getBigDecimal(1));
		
		return item;

	}

	/**
	 * Esse retorna o valor de um campo buscado da inst�ncia de um registro da pe�a
	 * ou, caso este seja nulo, retorna 0 (zero).
	 * 
	 * @param iteVO inst�ncia de um registro da pe�a.
	 * @param field campo a ser buscado.
	 * @return retorna o valor do campo buscado ou 0 (zero).
	 */
	@SuppressWarnings("unused")
	private static BigDecimal coalesce(DynamicVO iteVO, String field) {
		return iteVO.asBigDecimal(field) == null ? BigDecimal.ZERO : iteVO.asBigDecimal(field);
	}
}
