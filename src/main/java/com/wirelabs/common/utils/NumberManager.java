package com.wirelabs.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Component
public class NumberManager {

    public static final Random NUMBER_RANDOM = new Random(1);

    @Autowired
    public StringManager stringManager;

    /**
     * Converte um objeto <code>BigDecimal</code> em uma <code>String</code>
     *
     * @param value
     *            objeto <code>BigDecimal</code> com o valor a ser convertido
     * @param mask
     *            mascara utilizada para conversao<br/>
     * @return <code>String</code> com o valor formatado<br/>
     * @see java.text.DecimalFormat#format(Object)
     */
    public String bigDecimalToString(final BigDecimal value, final String mask) {
        String result = "";

        try {
            final DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(new Locale("pt", "BR"));
            df.applyPattern(mask);
            result = df.format(value == null ? BigDecimal.ZERO : value);
        } catch (final Exception e) {
            result = null;
        }
        return result;
    }

    public BigDecimal ceil(BigDecimal value, int decimalNumber) {
        return new BigDecimal(value.setScale(decimalNumber, RoundingMode.HALF_UP)
                .toString());
    }

    public BigDecimal divide(BigDecimal value, BigDecimal divisor, int decimalNumber) {
        BigDecimal result;

        final int decimal = decimalNumber < 0 ? -decimalNumber : decimalNumber;

        try {
            if (value.compareTo(BigDecimal.ZERO) == 0 || divisor.compareTo(BigDecimal.ZERO) == 0) {
                result = new BigDecimal("0");
            } else {
                result = value.divide(divisor, decimal + 1, BigDecimal.ROUND_HALF_UP);
            }
        } catch (final Exception e) {
            result = new BigDecimal("0");
        }

        if (result.compareTo(BigDecimal.ZERO) != 0 && decimalNumber > 0) {
            result = ceil(result, decimalNumber);
        } else {
            result = floor(result, -decimalNumber);
        }

        return result;
    }

    /**
     * Trunca casas decimais<br/>
     *
     * @param value
     *            valor a ser truncado<br/>
     * @param decimalNumber
     *            numero de casas decimais<br/>
     * @return valor com decimais truncado<br/>
     */
    public BigDecimal floor(BigDecimal value, int decimalNumber) {
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return new BigDecimal(value.toString());
        }

        final String mask = stringManager.replicate("0", value.toString()
                .length()) + "." + stringManager.replicate("0", 16);

        final String result = bigDecimalToString(value, mask).replace(",", ".");

        final int tamDigit = value.compareTo(BigDecimal.ZERO) < 0 ? 15 : 16;

        return new BigDecimal(result.substring(0, mask.length() - tamDigit + decimalNumber));
    }

    /**
     * Retorna valor String formatado de acordo com BigDecimal informado com mascara
     * ###,##0.00
     *
     * @param arg0
     * @return
     */
    public String formatCurrencyValue(BigDecimal arg0) {
        if (arg0 == null) {
            arg0 = getZero();
        }

        final DecimalFormat df = new DecimalFormat("###,##0.00");
        final DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator(',');
        df.setDecimalFormatSymbols(dfs);
        return df.format(arg0);
    }

    /**
     * Retorna valor String formatado de acordo com double informado com mascara
     * ###,##0.00
     *
     * @param arg0
     * @return
     */
    public String formatCurrencyValue(Double arg0) {
        if (arg0 == null) {
            arg0 = 0.0;
        }

        final DecimalFormat df = new DecimalFormat("###,##0.00");
        final DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator(',');
        df.setDecimalFormatSymbols(dfs);
        return df.format(arg0);
    }

    public String formatIntegerValue(final double arg0) {
        final DecimalFormat df = new DecimalFormat("#0");
        return df.format(arg0);
    }

    /**
     * Retorna <code>long</code> em formato <code>Binario</code><br/>
     *
     * @param value
     *            <code>Integer</code> a ser retornado o valor <code>Binario</code>
     * @param beginBit
     *            <code>Integer</code> bit inicial
     * @param endBit
     *            <code>Integer</code> bit final
     * @return <code>long</code> com a sequencia <code>Binario</code> ou <b>-1</b>
     *         em caso de erro
     */
    public long getBits(final int value, final int beginBit, final int endBit) {
        if (beginBit < 0 || endBit > 15 || beginBit > endBit) {
            return -1;
        }

        final StringBuilder result = new StringBuilder();

        final int[] factor = new int[] {
                1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768
        };

        for (int pos = endBit; pos >= beginBit; pos--) {
            result.append((value & factor[pos]) == factor[pos] ? "1" : "0");
        }

        return strToBigInteger(result.toString()).longValue();
    }

    /**
     * Retorna um BigDecimal genérico para número cem
     *
     * @return <code>BigDecimal</code> com o valor 100<br/>
     */
    public BigDecimal getCem() {
        return new BigDecimal("100");
    }

    /**
     * Retorna apenas os numeros de <b>0-9</b> de uma <code>String</code><br/>
     *
     * @param value
     *            <code>String</code> para extracao dos numeros<br/>
     * @return numeros de <b>0-9</b><br/>
     * @see #getNumbers(String, String)
     */
    public String getNumbers(final String value) {
        return getNumbers(value, "");
    }

    /**
     * Retorna os numeros de <b>0-9</b> e os simbolos/digitos definidos como excecao
     * de uma <code>String</code><br/>
     *
     * @param value
     *            <code>String</code> para extracao dos numeros<br/>
     * @param exception
     *            simbolos/digitos que tambem seram aceitos alem dos numeros
     * @return numeros de <b>0-9</b> e os simbolos/digitos definidos como
     *         excecao<br/>
     */
    public String getNumbers(final String value, final String exception) {
        final StringBuilder result = new StringBuilder("");
        char x;

        if (value != null) {
            for (int i = 0; i < value.length(); i++) {
                x = value.charAt(i);
                if (x >= '0' && x <= '9' || exception.length() > 0 && exception.indexOf(x) >= 0) {
                    result.append(x);
                }
            }
        }

        return result.toString();
    }

    /**
     * Retorna um BigDecimal genérico para número um
     *
     * @return <code>BigDecimal</code> com o valor 1<br/>
     */
    public BigDecimal getUm() {
        return new BigDecimal("1");
    }

    /**
     * Retorna um BigDecimal genérico para número zero
     *
     * @return <code>BigDecimal</code> com o valor zerado<br/>
     */
    public BigDecimal getZero() {
        return new BigDecimal("0.");
    }

    /**
     * Retorna se o primeiro elemento é igual ao segundo
     *
     * @param arg0
     *            primeiro elemento
     * @param arg1
     *            segundo elemento
     * @return Boolean true se for igual
     */
    public boolean igual(final BigDecimal firstElement, final BigDecimal secondElement) {
        final BigDecimal primeiro = firstElement != null ? firstElement : new BigDecimal("0.");
        final BigDecimal segundo = secondElement != null ? secondElement : new BigDecimal("0.");
        return primeiro.compareTo(segundo) == 0;
    }

    /**
     * Verifica se um unico item de um varargs de <code>BigDecimal</code> e null ou
     * equivale a 0
     *
     * @param vargargs
     *            de BigDecimal
     * @return boolean se existe ou nao um elemento vazio ou nulo.
     */
    public Boolean isNullOrZero(final BigDecimal... arrayBigDecimal) {
        for (final BigDecimal item : arrayBigDecimal) {
            if (item == null || item.compareTo(BigDecimal.ZERO) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica se um unico item de um varargs de <code>Big Integer</code> e null ou
     * equivale a 0
     *
     * @param vargargs
     *            de biginteger
     * @return boolean se existe ou nao um elemento vazio ou nulo.
     */
    public Boolean isNullOrZero(final BigInteger... arrayBigInteger) {
        for (final BigInteger item : arrayBigInteger) {
            if (item == null || item.compareTo(BigInteger.valueOf(0)) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica se um unico item de um varargs de <code>Big Integer</code> e null ou
     * equivale a 0
     *
     * @param vargargs
     *            de biginteger
     * @return boolean se existe ou nao um elemento vazio ou nulo.
     */
    public boolean isNullOrLessZero(final BigInteger... arrayBigInteger) {
        for (final BigInteger item : arrayBigInteger) {
            if (item == null || item.compareTo(BigInteger.valueOf(0)) < 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica se um unico item de um varargs de <code>Double</code> e null ou
     * equivale a 0
     *
     * @param vargargs
     *            de Double
     * @return boolean se existe ou nao um elemento vazio ou nulo.
     */
    public Boolean isNullOrZero(final Double... arrayDouble) {
        for (final Double item : arrayDouble) {
            if (item == null || item.equals(0.)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica se um unico item de um varargs de <code>Integer</code> e null ou
     * equivale a 0
     *
     * @param vargargs
     *            de Integer
     * @return boolean se existe ou nao um elemento vazio ou nulo.
     */
    public Boolean isNullOrZero(final Integer... arrayInteger) {
        for (final Integer item : arrayInteger) {
            if (item == null || item.equals(0)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica se um unico item de um varargs de <code>Integer</code> e null ou
     * menor que dois Otimo para validar ordenações que se iniciam em 1
     *
     * @param vargargs
     *            de Integer
     * @return boolean se existe ou nao um elemento vazio ou nulo.
     */
    public Boolean isNullOrLessTwo(final Integer... arrayInteger) {
        for (final Integer item : arrayInteger) {
            if (item == null || item < 2) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retorna se o primeiro elemento é maior ao segundo
     *
     * @param arg0
     *            primeiro elemento
     * @param arg1
     *            segundo elemento
     * @return Boolean true se for maior
     */
    public boolean maior(final BigDecimal firstElement, final BigDecimal secondElement) {
        final BigDecimal primeiro = firstElement != null ? firstElement : new BigDecimal("0.");
        final BigDecimal segundo = secondElement != null ? secondElement : new BigDecimal("0.");
        return primeiro.compareTo(segundo) > 0;
    }

    /**
     * Retorna se o primeiro elemento é maior ou igual ao segundo
     *
     * @param arg0
     *            primeiro elemento
     * @param arg1
     *            segundo elemento
     * @return Boolean true se for maior ou igual
     */
    public boolean maiorOuIgual(final BigDecimal firstElement, final BigDecimal secondElement) {
        return maior(firstElement, secondElement) || igual(firstElement, secondElement);
    }

    /**
     * Retorna o maior valor <code>BigDecimal</code> contido no
     * <code>Array BigDecimal</code>
     *
     * @param values
     *            <code>Array BigDecimal</code> com valores
     * @return <code>BigDecimal</code> maior valor decimal encontrado
     */
    public BigDecimal maxBigDecimal(final BigDecimal[] values) {
        BigDecimal result = values[0];

        for (final BigDecimal x : values) {
            result = x.compareTo(result) > 0 ? x : result;
        }

        return new BigDecimal(result.toString());
    }

    /**
     * Retorna se o primeiro elemento é menor ao segundo
     *
     * @param arg0
     *            primeiro elemento
     * @param arg1
     *            segundo elemento
     * @return Boolean true se for menor
     */
    public boolean menor(final BigDecimal firstElement, final BigDecimal secondElement) {
        final BigDecimal primeiro = firstElement != null ? firstElement : new BigDecimal("0.");
        final BigDecimal segundo = secondElement != null ? secondElement : new BigDecimal("0.");
        return primeiro.compareTo(segundo) < 0;
    }

    /**
     * Retorna se o primeiro elemento é menor ou igual ao segundo
     *
     * @param arg0
     *            primeiro elemento
     * @param arg1
     *            segundo elemento
     * @return Boolean true se for menor ou igual
     */
    public boolean menorOuIgual(final BigDecimal firstElement, final BigDecimal secondElement) {
        return menor(firstElement, secondElement) || igual(firstElement, secondElement);
    }

    public BigDecimal multiply(BigDecimal value, BigDecimal multiplicand, int decimalNumber) {
        BigDecimal result = new BigDecimal("0");

        try {
            if (value.compareTo(BigDecimal.ZERO) == 0 || multiplicand.compareTo(BigDecimal.ZERO) == 0) {
                result = new BigDecimal("0");
            } else {
                result = value.multiply(multiplicand);
            }
        } catch (final Exception e) {
            result = new BigDecimal("0");
        }

        if (result.compareTo(BigDecimal.ZERO) != 0 && decimalNumber > 0) {
            result = ceil(result, decimalNumber);
        } else {
            result = floor(result, -decimalNumber);
        }

        return result;
    }

    /**
     * Retorna um valor arrendondado
     *
     * @param arg1
     *            Valor
     * @param arg2
     *            numero de casas decimais
     */
    public BigDecimal round(final BigDecimal arg1, final int arg2) {
        BigDecimal roundedValue = BigDecimal.ZERO;

        try {
            roundedValue = arg1.setScale(arg2, BigDecimal.ROUND_HALF_UP);
        } catch (final Exception e) {
            e.printStackTrace();
            return arg1;
        }

        return roundedValue;
    }

    /**
     * Retorna um valor arrendondado
     *
     * @param arg1
     *            Valor
     * @param arg2
     *            numero de casas decimais
     */
    public Double round(final double arg1, final int arg2) {
        return round(new BigDecimal(arg1, MathContext.DECIMAL64), arg2).doubleValue();
    }

    /**
     * Arredonda para baixo um Double informado.
     *
     * @param value
     * @return
     */
    public double roundDown(final double value) {
        return Math.floor(value);
    }

    /**
     * Retorna double arredondado
     *
     * @param value
     * @return
     */
    public int roundInt(final double value) {
        return (int) Math.round(value);
    }

    /**
     * Arredonda para cima um Double informado.
     *
     * @param value
     * @return
     */
    public double roundUp(final double value) {
        return Math.ceil(value);
    }

    /**
     * Retorna um bigdecimal atraves de uma string
     *
     * @param value
     * @return
     */
    public BigDecimal stringToBigDecimal(String value) throws Exception {
        try {
            if (stringManager.isNullOrTrimEmpty(value)) {
                return null;
            }
            return new BigDecimal(stringManager.limpaStringMonetario(value));
        } catch (Exception e) {
            throw new Exception("Erro na conversao Bigdecimal", e);
        }
    }

    /**
     * Converte um conteudo <code>String</code> para <code>BigInteger</code>
     *
     * @param value
     *            valor valido para conversao<br/>
     * @return valor <code>BigInteger</code> convertido ou
     *         <code>BigInteger.ZERO</code> em caso de erro na conversao<br/>
     * @see java.math.BigInteger
     * @see java.math.BigInteger#ZERO
     */
    public BigInteger strToBigInteger(final String value) {
        BigInteger result = BigInteger.valueOf(0);
        final boolean isNeg = value.indexOf('-') >= 0;

        try {
            result = new BigInteger(getNumbers(value));
        } catch (final Exception e) {
            result = BigInteger.valueOf(0);
        }

        if (result.compareTo(BigInteger.ZERO) > 0 && isNeg) {
            result = result.negate();
        }

        return result;
    }

    /**
     * Converte um conteudo <code>String</code> para <code>int</code><br/>
     *
     * @param value
     *            valor valido para conversao<br/>
     * @return valor <code>int</code> convertido ou <code>ZERO</code> em caso de
     *         erro na conversao<br/>
     */
    public int strToInt(final String value) {
        int result = 0;
        final boolean isNeg = value.indexOf('-') >= 0;

        try {
            result = Integer.parseInt(getNumbers(value));
        } catch (final Exception e) {
            result = 0;
        }

        if (result > 0 && isNeg) {
            result *= -1;
        }

        return result;
    }

    /**
     * Retorna uma lista da Integer baseado em um string com números separados por
     * vírgula
     *
     * @param listString
     *            String contendo números, separados por vírgula
     * @return lista de inteiros
     */
    public List<Integer> toIntegerList(final String listString) {
        final List<Integer> list = new ArrayList<>();
        if (stringManager.isNullOrEmpty(listString)) {
            for (final String value : listString.split(",")) {
                list.add(new Integer(value.trim()));
            }
        }

        return list;
    }

    /**
     * Retorna o Valor Double Truncado
     *
     * @param double
     *            a ser truncado
     * @param quantidade
     *            de casas a serem truncadas.
     * @return Double Truncado.
     */
    public double trunc(final Double arg0, final int arg1) {
        if (arg0 == 0) {
            return 0;
        }

        String valor = String.valueOf(arg0);
        final int index = valor.indexOf('.');

        if (index < 0) {
            return arg0;
        }

        valor += "0000000000";
        return Double.parseDouble(valor.substring(0, index + 1 + arg1));
    }

}
