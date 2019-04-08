package com.wirelabs.common.utils;

import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.List;
import java.util.Random;

@Component
public class StringManager {

	public static final String NUL = "" + (char) 0;

	public static final String SOH = "" + (char) 1;

	public static final String STX = "" + (char) 2;

	public static final String ETX = "" + (char) 3;

	public static final String EOT = "" + (char) 4;

	public static final String ENQ = "" + (char) 5;

	public static final String ACK = "" + (char) 6;

	public static final String BEL = "" + (char) 7;

	public static final String BS = "" + (char) 8;

	public static final String CR = "" + (char) 13;

	public static final String SO = "" + (char) 14;

	public static final String LF = "" + (char) 10;

	public static final String DC4 = "" + (char) 20;

	public static final String NAK = "" + (char) 21;

	public static final String CAN = "" + (char) 24;

	public static final String ESC = "" + (char) 27;

	public static final String FS = "" + (char) 28;

	public static final String GS = "" + (char) 29;

	public static final String FF = "" + (char) 255;

	private static final String MASK = "ÃÕÀÈÌÒÙÁÉÍÓÚÄËÏÖÜÂÊÎÔÛÇ";

	private static final String CORRECT = "AOAEIOUAEIOUAEIOUAEIOUC";

	private static StringManager instance;

	public static StringManager get() {
		if (instance == null) {
			instance = new StringManager();
		}
		return instance;
	}

	/**
	 * Line Separator do SO
	 */
	public static final String LINE_FEED = System.getProperty("line.separator");

	/**
	 * Retorna um <code>String</code> criado a partir do <code>byte[]</code>
	 * fornecido<br/>
	 *
	 * @param value  <code>byte[]</code> a ser convertido
	 * @param length tamanho do <code>byte[]</code> a ser convertido
	 * @return <code>String</code> criado
	 */
	public String byteArrayToString(final byte[] value, final int length) {
		if (value == null) {
			return null;
		}

		final StringBuilder result = new StringBuilder();

		for (int pos = 0; pos < length && pos < value.length; pos++) {
			result.append((char) (value[pos] & 0xFF));
		}

		return result.toString();
	}

	public String capitalize(final String arg0) {
		if (arg0 == null) {
			return "";
		}

		final char chars[] = arg0.toLowerCase().replaceAll("#", " ").toCharArray();

		for (int i = 0; i < arg0.length(); i++) {
			if (i == 0 || chars[i - 1] == " ".toCharArray()[0]) {
				chars[i] = Character.toUpperCase(chars[i]);
			}
		}

		return new String(chars);
	}

	public String concat(final String... strings) {
		final StringBuilder result = new StringBuilder();
		for (final String item : strings) {
			if (!isNullOrEmpty(item)) {
				result.append(item + " ");
			}
		}

		return result.toString().trim();
	}

	/**
	 * Realiza replace de espaços duplos para 1 espaço
	 *
	 * @param string
	 * @return
	 *
	 * @author RAL
	 */
	public String duplicitTrim(final Object arg0) {
		String result = arg0 != null ? arg0.toString().trim() : "";
		result = result.replace("  ", " ");

		return result;
	}

	public String fillLeft(final String arg0, final String arg1, final int arg2) {
		final String value = arg0 != null ? arg0.trim() : "";
		String s = arg1;

		if (s == null) {
			s = " ";
		}

		final StringBuilder result = new StringBuilder();

		while (result.length() < arg2 - value.length()) {
			if (result.length() > 0 && String.valueOf(result.charAt(0)).equals("-")) {
				result.append("0");
			} else {
				result.append(s);
			}
		}

		result.append(value);

		return result.length() == arg2 ? result.toString() : result.substring(0, arg2);
	}

	public String fillRight(final String arg0, final String arg1, final int arg2) {
		String value = arg0 != null ? arg0.trim() : "";
		value = formatString(value);

		String s = arg1;

		if (s == null) {
			s = " ";
		}

		final StringBuilder result = new StringBuilder(value);

		while (result.length() < arg2) {
			result.append(s);
		}

		return result.length() == arg2 ? result.toString() : result.substring(0, arg2);
	}

	public String formatString(final String arg0) {
		String result = arg0;

		if (result != null) {
			for (int i = 0; i < MASK.length(); i++) {
				result = result.replaceAll(String.valueOf(MASK.charAt(i)), String.valueOf(CORRECT.charAt(i)));
				result = result.replaceAll(String.valueOf(MASK.toLowerCase().charAt(i)),
						String.valueOf(CORRECT.toLowerCase().charAt(i)));
			}

			return result.replaceAll("[^\\p{ASCII}]", "");
		}

		return "";
	}

	/**
	 * Retorna um <code>String</code> formatado de acordo com a mascara<br/>
	 * A mascara de formatacao utiliza os caracteres <b>0</b>, <b>#</b>, <b>X</b> ou
	 * <b>!</b> como controle.
	 *
	 * @param value <code>String</code> a ser formatado
	 * @param mask  <code>String</code> com a mascara a ser utilizada
	 * @return Exemplos:<br/>
	 *         <br/>
	 *         formatString(" 1","000") returns "001"<br/>
	 *         formatString(" 1","##0") returns "1"<br/>
	 *         formatString("123456","00/00/00") returns "12/34/56"<br/>
	 *         formatString("12345678901","999.999.999-99") returns
	 *         "123.456.789-01"<br/>
	 *         formatString("uppercase","!!!!!!!!!") returns "UPPERCASE"<br/>
	 *         <br/>
	 */
	public String formatString(final String value, final String mask) {
		if (value == null || mask == null) {
			return null;
		}

		final StringBuilder result = new StringBuilder();
		String byteMask;
		String byteValue;
		int pos = 0;

		for (int i = 0; i < mask.length(); i++) {
			byteMask = mask.substring(i, i + 1);
			byteValue = value.substring(pos, pos + 1);

			if (byteMask.contains("0") || byteMask.contains("#") || byteMask.contains("X") || byteMask.contains("!")) {
				if (byteMask.contains("0") && byteValue.trim().length() == 0) {
					byteValue = "0";
				} else if (byteMask.contains("#") && byteValue.trim().length() == 0) {
					byteValue = "";
				} else if (byteMask.contains("!")) {
					byteValue = byteValue.toUpperCase();
				}

				if (byteValue.length() > 0) {
					result.append(byteValue);
				}

				pos++;
			} else {
				result.append(byteMask);
				if (byteValue.equals(byteMask)) {
					pos++;
				}
			}
		}

		return result.toString();
	}

	public String fromIntegerList(final List<Integer> array) {
		return array.toString().replace("[", "").replace("]", "");

	}

	/**
	 * Retorna o conteudo do <code>PrintStackTrace</code> em uma
	 * <code>String</code><br/>
	 *
	 * @param aThrowable objeto <code>Throwable</code> referente a excecao ocorrida
	 * @return <code>String</code> com o conteudo do
	 *         <code>PrintStackTrace</code><br/>
	 * @see Throwable#printStackTrace(java.io.PrintStream)
	 */
	public String getStackTrace(final Throwable aThrowable) {
		if (aThrowable == null) {
			return null;
		}

		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}

	public boolean isEmpty(final Object arg0) {
		if (arg0 == null) {
			return true;
		}

		return "".equals(trim(arg0));
	}

	public boolean isEmpty(final String arg0) {
		return "".equals(trim(arg0));
	}

	public Boolean isNullOrEmpty(final String... arrayStrings) {

		for (final String item : arrayStrings) {
			if (item == null || item.equals("") || item.equals("null")) {
				return true;
			}
		}
		return false;
	}

	public Boolean isNullOrTrimEmpty(final String... arrayStrings) {
		for (final String item : arrayStrings) {
			if (item == null || item.equals("null") || item.trim().equals("")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Método que verifica se uma String contém somente números para que possa ser
	 * tratada como tal
	 *
	 * @param str String a ser verificada
	 * @return true se contém somente números, false se contém qualquer outro tipo
	 *         de caractere
	 * @author Rafael Ingaramo
	 */
	public boolean isNumber(String str) {
		if (str == null) {
			return false;
		}

		return str.matches("\\d+");
	}

	/**
	 * Metodo para limpeza de String de cep formatado
	 *
	 * @param cep
	 * @return cep sem formatcao
	 */
	public String limpaStringCep(final String cep) {
		return cep.replace("-", "").trim();
	}

	/**
	 * Metodo para limpeza de String de cep formatado
	 *
	 * @param cep
	 * @return cep sem formatcao
	 */
	public String limpaStringMonetario(final String mon) {
		return mon.replace(",", ".").trim();
	}

	/**
	 * Metodo para limpeza de String de telefone formatado
	 *
	 * @param telefone
	 * @return telefone sem formatcao
	 */
	public String limpaStringTelefone(final String telefone) {
		return telefone.replace("(", "").replace(")", "").replace("-", "").trim();
	}

	public String mixCase(final String arg0) {
		String result = arg0.toLowerCase();

		for (int i = 1; i <= arg0.length() / 5; i++) {
			int pos = new Random().nextInt(arg0.length());

			if (pos > 0) {
				pos--;
			}

			final String s = result.substring(pos, pos + 1);

			if (pos == 0) {
				result = s.toUpperCase() + result.substring(pos + 1);
			} else {
				result = result.substring(0, pos) + s.toUpperCase() + result.substring(pos + 1);
			}
		}

		return result;
	}

	/**
	 * Método que faz replaceall em todos os caracteres que não são números e mantém
	 * só caracteres númericos
	 *
	 * @param str String a ser tratada
	 * @return String tratada
	 * @author Rafael Ingaramo
	 */
	public String onlyDigits(String str) {
		if (str == null) {
			return null;
		}

		return str.replaceAll("\\D", "");
	}

	/**
	 * Retorna um <code>String</code> preenchido a esquerda no tamanho
	 * determinado<br/>
	 * Se o tamanho determinado for menor que o tamanho original do
	 * <code>String</code>, sera retornado um novo <code>String</code> truncado
	 *
	 * @param value    <code>String</code> a ser preenchido a esquerda
	 * @param fillChar <code>String</code> a ser utilizado como preenchimento
	 * @param length   tamanho do <code>String</code> a ser retornado preenchida ou
	 *                 truncado a direita<br/>
	 * @return Exemplos:<br/>
	 *         <br/>
	 *         padLeft("happy","*",10) returns "*****happy"<br/>
	 *         padLeft("unhappy","*",5) returns "unhap"<br/>
	 *         <br/>
	 */
	public String padLeft(final String value, String fillChar, final int length) {
		final StringBuilder result = new StringBuilder(value == null ? "" : value);

		if (fillChar.length() < 1) {
			// Enviou errado, nao pode ser String tamanho zero
			fillChar = " ";
		}

		while (result.length() < length) {
			result.insert(0, fillChar);
		}

		return result.toString().substring(0, length);
	}

	/**
	 * Retorna um <code>String</code> preenchido a direita no tamanho
	 * determinado<br/>
	 * Se o tamanho determinado for menor que o tamanho original do
	 * <code>String</code>, sera retornado um novo <code>String</code> truncado
	 *
	 * @param value    <code>String</code> a ser preenchido a direita
	 * @param fillChar <code>String</code> a ser utilizado como preenchimento
	 * @param length   tamanho do <code>String</code> a ser retornado preenchida ou
	 *                 truncado a direita<br/>
	 * @return Exemplos:<br/>
	 *         <br/>
	 *         padRight("happy","*",10) returns "happy*****"<br/>
	 *         padRight("unhappy","*",5) returns "unhap"<br/>
	 *         <br/>
	 */
	public String padRight(final String value, String fillChar, final int length) {
		final StringBuilder result = new StringBuilder(value == null ? "" : value);

		if (fillChar.length() < 1) {
			// Enviou errado, nao pode ser String tamanho zero
			fillChar = " ";
		}

		while (result.length() < length) {
			result.append(fillChar);
		}

		return result.toString().substring(0, length);
	}

	public String quoteDouble(final String arg0) {
		return (char) 34 + arg0 + (char) 34;
	}

	public String removeAccents(String s) {

		if (s != null && !s.isEmpty()) {

			s = Normalizer.normalize(s, Normalizer.Form.NFD);
			s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
			return s;

		}

		return null;
	}

	/**
	 * Retorna um <code>String</code> removendo os caracteres informados<br/>
	 *
	 * @param value        <code>String</code> a ser analisada
	 * @param charToRemove <code>String</code> com os caracteres a serem removidos
	 * @return Exemplos:<br/>
	 *         <br/>
	 *         removeChar("1-2/3.4","-/.") returns "1234"<br/>
	 *         <br/>
	 */
	public String removeChar(final String value, final String charToRemove) {
		if (value == null) {
			return null;
		}

		final StringBuilder result = new StringBuilder();
		boolean byteOk;

		for (int i = 0; i < value.length(); i++) {
			byteOk = true;

			for (int x = 0; x < charToRemove.length() && byteOk; x++) {
				if (value.substring(i, i + 1).compareTo(charToRemove.substring(x, x + 1)) == 0) {
					byteOk = false;
				}

			}

			if (byteOk) {
				result.append(value.substring(i, i + 1));
			}
		}

		return result.toString();

	}

	public String removeFirstAndLastChar(final String arg0) {
		return arg0.substring(1, arg0.length() - 1);
	}

	public String removeLastChar(final String arg0) {
		return removeLastChar(arg0, 1);
	}

	public String removeLastChar(final String arg0, final int arg1) {
		return arg0.substring(0, arg0.length() - arg1);
	}

	public String removeLineBreaks(final Object arg0) {
		String result = trim(arg0);

		while (result.indexOf('\n') >= 0) {
			result = result.replaceAll("\n", " ");
		}

		return trim(result);
	}

	/**
	 * Cria uma nova <code>String</code> replicada o numero de vezes informado
	 *
	 * @param value <code>String</code> a ser replicada
	 * @param count quantidade de replicacoes<br/>
	 * @return <code>String</code> replicada <code>count</code> vezes
	 */
	public String replicate(final String value, final int count) {
		if (value == null) {
			return null;
		}

		final StringBuilder result = new StringBuilder();
		for (int i = 0; i < count; i++) {
			result.append(value);
		}

		return result.toString();
	}

	/**
	 * Retorna uma <code>String</code> com a quantidade de bytes informada a partir
	 * da direita <br/>
	 * Exemplos:<br/>
	 * <br/>
	 * right("unhappy",2) returns "py"<br/>
	 * right("unhappy",10) returns "unhappy"<br/>
	 * right("Harbison",3,0) returns "son"<br/>
	 * substring("emptiness",0) returns "" (espaco nulo)<br/>
	 * substring(<code>null</code>,9,0) returns "" (espaco nulo)<br/>
	 * <br/>
	 *
	 * @param value  <code>String</code> original
	 * @param length quantidade de bytes a partir da posicao final<br/>
	 * @return nova <code>String</code> gerada a partir das informacoes ou
	 *         <code>espaco nulo</code><br/>
	 */
	public String right(final String value, final int length) {
		String result = "";

		if (value != null && length > 0 && value.length() > 0) {
			if (length > value.length()) {
				result = value;
			} else {
				result = value.substring(value.length() - length);
			}
		}

		return result;
	}

	/**
	 * Retorna String.trim(), se toTrim for igual a null, retorna null.
	 *
	 * @author Rafael Ingaramo
	 */
	public String safeTrim(final String toTrim) {
		return toTrim == null ? null : toTrim.trim();
	}

	/**
	 * Procura em <code>String[]</code> na primeira posicao de cada linha por
	 * <code>charToLocate</code>
	 *
	 * @param value        <code>String[]</code> com as linhas
	 * @param charToLocate <code>String</code> a ser pesquisado
	 * @param initialPos   posicao inicial para iniciar a procura
	 * @return posicao do <code>String[]</code> onde foi encontrado ou
	 *         <code>-1</code> caso nao encontre
	 */
	public int stringArraySearchFirstChar(final String[] value, final String charToLocate, final int initialPos) {
		if (value == null || charToLocate == null) {
			return -1;
		}

		for (int i = initialPos; i < value.length; i++) {
			if (value[i].substring(0, 1).equalsIgnoreCase(charToLocate)) {
				return i;
			}
		}

		for (int i = 0; i < initialPos; i++) {
			if (value[i].substring(0, 1).equalsIgnoreCase(charToLocate)) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Procura em <code>String[]</code> por <code>stringToLocate</code>
	 *
	 * @param value          <code>String[]</code> com as linhas
	 * @param stringToLocate <code>String</code> a ser pesquisado
	 * @param initialPos     posicao inicial para iniciar a procura
	 * @return posicao do <code>String[]</code> onde foi encontrado ou
	 *         <code>-1</code> caso nao encontre
	 */
	public int stringArraySearchString(final String[] value, final String stringToLocate, final int initialPos) {
		if (value == null || stringToLocate == null) {
			return -1;
		}

		for (int i = initialPos; i < value.length; i++) {
			if (value[i].equalsIgnoreCase(stringToLocate)) {
				return i;
			}
		}

		for (int i = 0; i < initialPos; i++) {
			if (value[i].equalsIgnoreCase(stringToLocate)) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Troca os caracteres acentuados em <code>String</code> por caracteres nao
	 * acentuados e possibilita a remocao de caracteres nao imprimiveis <b>(ASC >
	 * 127)</b><br/>
	 * De: <b>ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï
	 * ¿½ï¿ ½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
	 * ï¿½ï¿½ï¿½ï¿½</b><br/>
	 * Para: <b>aaaaaceeeeiiiinooooouuuuyyAAAAACEEEEIIIINOOOOOUUUUY</b><br/>
	 *
	 * @param value              <code>String</code> a ser analisada
	 * @param removeSpecialChars <code>Boolean</code> remove todos os caracteres ASC
	 *                           > 127<br/>
	 * @return <code>String</code> sem os caracteres acentuados
	 */
	public String stripAccents(final String value, final boolean removeSpecialChars) {
		if (value == null) {
			return null;
		}

		final String inChars = "Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã�Âº";
		final String outChars = "aaaaaceeeeiiiinooooouuuuyyAAAAACEEEEIIIINOOOOOUUUUY.";
		final StringBuilder result = new StringBuilder(value);
		int pos;

		for (int i = 0; i < inChars.length(); i++) {
			pos = result.indexOf(inChars.substring(i, i + 1));
			while (pos >= 0) {
				result.replace(pos, pos + 1, outChars.substring(i, i + 1));
				pos = result.indexOf(inChars.substring(i, i + 1));
			}
		}

		// So remove os caracteres speciais depois de substituir
		if (removeSpecialChars) {
			pos = 0;

			while (pos < result.length()) {
				if (result.charAt(pos) > 127) {
					result.deleteCharAt(pos);
				} else {
					pos++;
				}
			}
		}

		return result.toString();
	}

	public String stripZero(String valor) {

		final BigDecimal value = new BigDecimal(valor);
		final DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(0);

		return df.format(value);

	}

	/**
	 * Metodo analogo ao <code>String#substring(int, int)</code><br/>
	 * Sua diferenca esta no fato de:<br/>
	 * <br/>
	 * <b>*</b> - Quando <code>beginIndex</code> maior que <code>value.length</code>
	 * retorna <code>espaco nulo</code> e nao gera excecao<br/>
	 * <b>*</b> - Quando <code>length</code> maior que <code>value.length</code>
	 * retorna <code>String</code> com tamanho maximo disponivel e nao gera
	 * excecao<br/>
	 * <b>*</b> - Quando <code>length</code> igual a <code>ZERO</code> retorna
	 * <code>String</code> com tamanho maximo disponivel a partir e inclusive
	 * <code>beginIndex</code> e nao gera excecao<br/>
	 * <br/>
	 * Exemplos:<br/>
	 * <br/>
	 * substring("unhappy",2,0) returns "happy"<br/>
	 * substring("unhappy",0,10) returns "unhappy"<br/>
	 * substring("Harbison",3,0) returns "bison"<br/>
	 * substring("Harbison",1,4) returns "arbi"<br/>
	 * substring("emptiness",9,9) returns "" (espaco nulo)<br/>
	 * substring("emptiness",9,0) returns "" (espaco nulo)<br/>
	 * <br/>
	 *
	 * @param value      <code>String</code> original
	 * @param beginIndex posicao inicial (inclusive). Posicao inicial de uma
	 *                   <code>String</code> comeca em <code>ZERO</code>
	 * @param length     quantidade de bytes a partir da posicao inicial ou
	 *                   <code>ZERO</code> para todos os bytes a partir da posicao
	 *                   inicial<br/>
	 * @return nova <code>String</code> gerada a partir das informacoes ou
	 *         <code>espaco nulo</code><br/>
	 * @see String#substring(int, int)
	 */
	public String substring(final String value, final int beginIndex, final int length) {
		String result = "";

		if (value != null && beginIndex >= 0 && beginIndex < value.length()) {
			if (length > 0 && beginIndex + length <= value.length()) {
				result = value.substring(beginIndex, beginIndex + length);
			} else {
				result = value.substring(beginIndex);
			}
		}

		return result;
	}

	public Double toDouble(String arg0) {
		if (arg0 == null) {
			arg0 = "0.0";
		}

		arg0 = arg0.replaceAll("\\.", "");
		arg0 = arg0.replaceAll(",", ".");
		return Double.valueOf(arg0);
	}

	public String toHex(final String arg) {
		return String.format("%040x", new BigInteger(1, arg.getBytes(StandardCharsets.UTF_8)));
	}

	public String trim(final Object arg0) {
		String result = arg0 != null ? arg0.toString().trim() : "";

		while (result.indexOf("  ") >= 0) {
			result = result.replaceAll("  ", " ");
		}

		return result;
	}

	public String trimAll(final Object arg0) {
		String result = arg0 != null ? arg0.toString().trim() : "";

		while (result.indexOf(' ') >= 0) {
			result = result.replaceAll(" ", "");
		}

		return result;
	}
}
