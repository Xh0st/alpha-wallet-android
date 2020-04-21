package com.alphawallet.app.entity.tokenscript;

import io.reactivex.Observable;

import com.alphawallet.app.entity.tokens.Token;
import com.alphawallet.app.repository.TokenRepository;
import com.alphawallet.app.util.BalanceUtils;
import com.alphawallet.app.util.Utils;
import com.alphawallet.token.entity.*;
import com.alphawallet.token.tools.TokenDefinition;

import io.reactivex.Single;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.BytesType;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Int;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Uint;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction;
import static org.web3j.tx.Contract.staticExtractEventParameters;

/**
 * Created by James on 13/06/2019.
 * Stormbird in Sydney
 */
public abstract class TokenscriptFunction
{
    public static final String TOKENSCRIPT_CONVERSION_ERROR = "<error>";

    public Function generateTransactionFunction(String walletAddress, BigInteger tokenId, TokenDefinition definition, FunctionDefinition function, AttributeInterface attrIf)
    {
        //pre-parse tokenId.
        if (tokenId.bitCount() > 256) tokenId = tokenId.or(BigInteger.ONE.shiftLeft(256).subtract(BigInteger.ONE)); //truncate tokenId too large
        if (walletAddress == null) walletAddress = ZERO_ADDRESS;

        List<Type> params = new ArrayList<Type>();
        List<TypeReference<?>> returnTypes = new ArrayList<TypeReference<?>>();
        for (MethodArg arg : function.parameters)
        {
            resolveReference(walletAddress, arg, tokenId, definition, attrIf);
            //get arg.element.value in the form of BigInteger if appropriate
            byte[] argValueBytes = convertArgToBytes(arg.element.value);
            BigInteger argValueBI = new BigInteger(1, argValueBytes);

            switch (arg.parameterType)
            {
                case "int":
                    params.add(new Int(argValueBI));
                    break;
                case "int8":
                    params.add(new Int8(argValueBI));
                    break;
                case "int16":
                    params.add(new Int16(argValueBI));
                    break;
                case "int24":
                    params.add(new Int24(argValueBI));
                    break;
                case "int32":
                    params.add(new Int32(argValueBI));
                    break;
                case "int40":
                    params.add(new Int40(argValueBI));
                    break;
                case "int48":
                    params.add(new Int48(argValueBI));
                    break;
                case "int56":
                    params.add(new Int56(argValueBI));
                    break;
                case "int64":
                    params.add(new Int64(argValueBI));
                    break;
                case "int72":
                    params.add(new Int72(argValueBI));
                    break;
                case "int80":
                    params.add(new Int80(argValueBI));
                    break;
                case "int88":
                    params.add(new Int88(argValueBI));
                    break;
                case "int96":
                    params.add(new Int96(argValueBI));
                    break;
                case "int104":
                    params.add(new Int104(argValueBI));
                    break;
                case "int112":
                    params.add(new Int112(argValueBI));
                    break;
                case "int120":
                    params.add(new Int120(argValueBI));
                    break;
                case "int128":
                    params.add(new Int128(argValueBI));
                    break;
                case "int136":
                    params.add(new Int136(argValueBI));
                    break;
                case "int144":
                    params.add(new Int144(argValueBI));
                    break;
                case "int152":
                    params.add(new Int152(argValueBI));
                    break;
                case "int160":
                    params.add(new Int160(argValueBI));
                    break;
                case "int168":
                    params.add(new Int168(argValueBI));
                    break;
                case "int176":
                    params.add(new Int176(argValueBI));
                    break;
                case "int184":
                    params.add(new Int184(argValueBI));
                    break;
                case "int192":
                    params.add(new Int192(argValueBI));
                    break;
                case "int200":
                    params.add(new Int200(argValueBI));
                    break;
                case "int208":
                    params.add(new Int208(argValueBI));
                    break;
                case "int216":
                    params.add(new Int216(argValueBI));
                    break;
                case "int224":
                    params.add(new Int224(argValueBI));
                    break;
                case "int232":
                    params.add(new Int232(argValueBI));
                    break;
                case "int240":
                    params.add(new Int240(argValueBI));
                    break;
                case "int248":
                    params.add(new Int248(argValueBI));
                    break;
                case "int256":
                    params.add(new Int256(argValueBI));
                    break;
                case "uint":
                    params.add(new Uint(argValueBI));
                    break;
                case "uint8":
                    params.add(new Uint8(argValueBI));
                    break;
                case "uint16":
                    params.add(new Uint16(argValueBI));
                    break;
                case "uint24":
                    params.add(new Uint24(argValueBI));
                    break;
                case "uint32":
                    params.add(new Uint32(argValueBI));
                    break;
                case "uint40":
                    params.add(new Uint40(argValueBI));
                    break;
                case "uint48":
                    params.add(new Uint48(argValueBI));
                    break;
                case "uint56":
                    params.add(new Uint56(argValueBI));
                    break;
                case "uint64":
                    params.add(new Uint64(argValueBI));
                    break;
                case "uint72":
                    params.add(new Uint72(argValueBI));
                    break;
                case "uint80":
                    params.add(new Uint80(argValueBI));
                    break;
                case "uint88":
                    params.add(new Uint88(argValueBI));
                    break;
                case "uint96":
                    params.add(new Uint96(argValueBI));
                    break;
                case "uint104":
                    params.add(new Uint104(argValueBI));
                    break;
                case "uint112":
                    params.add(new Uint112(argValueBI));
                    break;
                case "uint120":
                    params.add(new Uint120(argValueBI));
                    break;
                case "uint128":
                    params.add(new Uint128(argValueBI));
                    break;
                case "uint136":
                    params.add(new Uint136(argValueBI));
                    break;
                case "uint144":
                    params.add(new Uint144(argValueBI));
                    break;
                case "uint152":
                    params.add(new Uint152(argValueBI));
                    break;
                case "uint160":
                    params.add(new Uint160(argValueBI));
                    break;
                case "uint168":
                    params.add(new Uint168(argValueBI));
                    break;
                case "uint176":
                    params.add(new Uint176(argValueBI));
                    break;
                case "uint184":
                    params.add(new Uint184(argValueBI));
                    break;
                case "uint192":
                    params.add(new Uint192(argValueBI));
                    break;
                case "uint200":
                    params.add(new Uint200(argValueBI));
                    break;
                case "uint208":
                    params.add(new Uint208(argValueBI));
                    break;
                case "uint216":
                    params.add(new Uint216(argValueBI));
                    break;
                case "uint224":
                    params.add(new Uint224(argValueBI));
                    break;
                case "uint232":
                    params.add(new Uint232(argValueBI));
                    break;
                case "uint240":
                    params.add(new Uint240(argValueBI));
                    break;
                case "uint248":
                    params.add(new Uint248(argValueBI));
                    break;
                case "uint256":
                    switch (arg.element.ref)
                    {
                        case "tokenId":
                            params.add(new Uint256(tokenId));
                            break;
                        case "value":
                        default:
                            params.add(new Uint256(argValueBI));
                            break;
                    }
                    break;
                case "address":
                    switch (arg.element.ref)
                    {
                        case "ownerAddress":
                            params.add(new Address(walletAddress));
                            break;
                        case "value":
                        default:
                            params.add(new Address(Numeric.toHexString(argValueBytes)));
                            break;
                    }
                    break;
                case "string":
                    params.add(new Utf8String(arg.element.value));
                    break;
                case "bytes":
                    params.add(new BytesType(argValueBytes, "bytes"));
                    break;
                case "bytes1":
                    params.add(new Bytes1(argValueBytes));
                    break;
                case "bytes2":
                    params.add(new Bytes2(argValueBytes));
                    break;
                case "bytes3":
                    params.add(new Bytes3(argValueBytes));
                    break;
                case "bytes4":
                    params.add(new Bytes4(argValueBytes));
                    break;
                case "bytes5":
                    params.add(new Bytes5(argValueBytes));
                    break;
                case "bytes6":
                    params.add(new Bytes6(argValueBytes));
                    break;
                case "bytes7":
                    params.add(new Bytes7(argValueBytes));
                    break;
                case "bytes8":
                    params.add(new Bytes8(argValueBytes));
                    break;
                case "bytes9":
                    params.add(new Bytes9(argValueBytes));
                    break;
                case "bytes10":
                    params.add(new Bytes10(argValueBytes));
                    break;
                case "bytes11":
                    params.add(new Bytes11(argValueBytes));
                    break;
                case "bytes12":
                    params.add(new Bytes12(argValueBytes));
                    break;
                case "bytes13":
                    params.add(new Bytes13(argValueBytes));
                    break;
                case "bytes14":
                    params.add(new Bytes14(argValueBytes));
                    break;
                case "bytes15":
                    params.add(new Bytes15(argValueBytes));
                    break;
                case "bytes16":
                    params.add(new Bytes16(argValueBytes));
                    break;
                case "bytes17":
                    params.add(new Bytes17(argValueBytes));
                    break;
                case "bytes18":
                    params.add(new Bytes18(argValueBytes));
                    break;
                case "bytes19":
                    params.add(new Bytes19(argValueBytes));
                    break;
                case "bytes20":
                    params.add(new Bytes20(argValueBytes));
                    break;
                case "bytes21":
                    params.add(new Bytes21(argValueBytes));
                    break;
                case "bytes22":
                    params.add(new Bytes22(argValueBytes));
                    break;
                case "bytes23":
                    params.add(new Bytes23(argValueBytes));
                    break;
                case "bytes24":
                    params.add(new Bytes24(argValueBytes));
                    break;
                case "bytes25":
                    params.add(new Bytes25(argValueBytes));
                    break;
                case "bytes26":
                    params.add(new Bytes26(argValueBytes));
                    break;
                case "bytes27":
                    params.add(new Bytes27(argValueBytes));
                    break;
                case "bytes28":
                    params.add(new Bytes28(argValueBytes));
                    break;
                case "bytes29":
                    params.add(new Bytes29(argValueBytes));
                    break;
                case "bytes30":
                    params.add(new Bytes30(argValueBytes));
                    break;
                case "bytes31":
                    params.add(new Bytes31(argValueBytes));
                    break;
                case "bytes32": //sometimes tokenId can be passed as bytes32
                    switch (arg.element.ref)
                    {
                        case "tokenId":
                            params.add(new Bytes32(Numeric.toBytesPadded(tokenId, 32)));
                            break;
                        case "value":
                            params.add(new Bytes32(argValueBytes));
                            break;
                        default:
                            params.add(new Bytes32(Numeric.toBytesPadded(argValueBI, 32)));
                            break;
                    }
                    break;
                default:
                    System.out.println("NOT IMPLEMENTED: " + arg.parameterType);
                    break;
            }
        }
        switch (function.syntax)
        {
            //not used as of now
            case Boolean:
            case Integer:
            case NumericString:
                returnTypes.add(new TypeReference<Uint256>()
                {
                });
                break;
            case IA5String:
            case DirectoryString:
                returnTypes.add(new TypeReference<Utf8String>()
                {
                });
                break;
        }

        return new Function(function.method,
                            params, returnTypes);
    }

    public static byte[] convertArgToBytes(String inputValue)
    {
        byte[] argBytes = new byte[1];
        try
        {
            String hexValue = inputValue;
            if (!Numeric.containsHexPrefix(inputValue))
            {
                BigInteger value;
                try
                {
                    value = new BigInteger(inputValue);
                }
                catch (NumberFormatException e)
                {
                    e.printStackTrace();
                    value = new BigInteger(inputValue, 16);
                }

                hexValue = Numeric.toHexStringNoPrefix(value.toByteArray());
                //fix sign condition
                if (hexValue.length() > 64 && hexValue.startsWith("00"))
                {
                    hexValue = hexValue.substring(2);
                }
            }

            argBytes = Numeric.hexStringToByteArray(hexValue);
        }
        catch (Exception e)
        {
            //no action
        }

        return argBytes;
    }

    private void handleTransactionResult(TransactionResult result, Function function, String responseValue, FunctionDefinition fd, long lastTransactionTime)
    {
        try
        {
            //try to interpret the value
            List<Type> response = FunctionReturnDecoder.decode(responseValue, function.getOutputParameters());
            if (response.size() > 0)
            {
                result.resultTime = lastTransactionTime;
                Type val = response.get(0);
                switch (fd.syntax)
                {
                    case Boolean:
                        BigDecimal value = new BigDecimal(((Uint256) val).getValue());
                        result.result = value.equals(BigDecimal.ZERO) ? "FALSE" : "TRUE";
                        break;
                    case Integer:
                    case NumericString:
                        result.result = new BigDecimal(((Uint256) val).getValue()).toString();
                        break;
                    case IA5String:
                    case DirectoryString:
                        result.result = (String) response.get(0).getValue();
                        if (responseValue.length() > 2 && result.result.length() == 0)
                        {
                            result.result = checkBytesString(responseValue);
                        }
                        break;
                }
            }
            else
            {
                result.resultTime = 0;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private String checkBytesString(String responseValue) throws Exception
    {
        String name = "";
        if (responseValue.length() > 0)
        {
            //try raw bytes
            byte[] data = Numeric.hexStringToByteArray(responseValue);
            //check leading bytes for non-zero
            if (data[0] != 0)
            {
                //truncate zeros
                int index = data.length - 1;
                while (data[index] == 0 && index > 0)
                    index--;
                if (index != (data.length - 1))
                {
                    data = Arrays.copyOfRange(data, 0, index + 1);
                }
                name = new String(data, "UTF-8");
            }
        }

        return name;
    }

    public TokenScriptResult.Attribute parseFunctionResult(TransactionResult transactionResult, AttributeType attr)
    {
        String res = attr.getSyntaxVal(transactionResult.result);
        BigInteger val = transactionResult.tokenId; //?

        if (attr.syntax == TokenDefinition.Syntax.NumericString)
        {
            if (transactionResult.result == null)
            {
                res = "0";
            }
            else if (transactionResult.result.startsWith("0x"))
            {
                res = res.substring(2);
            }
            try
            {
                val = new BigInteger(res, 16);
            }
            catch (NumberFormatException e)
            {
                val = BigInteger.ZERO;
            }
        }
        return new TokenScriptResult.Attribute(attr.id, attr.name, val, res);
    }

    public static final String ZERO_ADDRESS = "0x0000000000000000000000000000000000000000";

    /**
     * Haven't pre-cached this value yet, so need to fetch it before we can proceed
     * @param override
     * @param attr
     * @param tokenId
     * @param definition
     * @return
     */
    public Observable<TransactionResult> fetchResultFromEthereum(String walletAddress, ContractAddress override, AttributeType attr,
                                                                 BigInteger tokenId, TokenDefinition definition, AttributeInterface attrIf, long lastTransactionTime)
    {
        return Observable.fromCallable(() -> {
            ContractAddress useAddress;
            if (override == null) // contract not specified - is not holder contract
            {
                //determine address using definition context
                useAddress = new ContractAddress(attr.function, definition.context.cAddr.chainId, definition.context.cAddr.address);
            }
            else
            {
                useAddress = override;
            }
            TransactionResult transactionResult = new TransactionResult(useAddress.chainId, useAddress.address, tokenId, attr);
            org.web3j.abi.datatypes.Function transaction = generateTransactionFunction(walletAddress, tokenId, definition, attr.function, attrIf);

            //now push the transaction
            String result = callSmartContractFunction(TokenRepository.getWeb3jService(useAddress.chainId), transaction, useAddress.address, ZERO_ADDRESS);

            handleTransactionResult(transactionResult, transaction, result, attr.function, lastTransactionTime);
            return transactionResult;
        });
    }

    private String callSmartContractFunction(Web3j web3j,
                                             Function function, String contractAddress, String walletAddr)
    {
        String encodedFunction = FunctionEncoder.encode(function);

        try
        {
            org.web3j.protocol.core.methods.request.Transaction transaction
                    = createEthCallTransaction(walletAddr, contractAddress, encodedFunction);
            EthCall response = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();

            return response.getValue();
        }
        catch (IOException e)
        {
            //Connection error. Use cached value
            return null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private void resolveReference(String walletAddress, MethodArg arg, BigInteger tokenId, TokenDefinition definition, AttributeInterface attrIf)
    {
        if (definition != null && definition.attributeTypes.containsKey(arg.element.ref))
        {
            arg.element.value = fetchAttrResult(walletAddress, definition.attributeTypes.get(arg.element.ref), tokenId, null, definition, attrIf, 0).blockingSingle().text;
        }
    }

    /**
     * Fetches a TokenScript attribute
     *
     * May either return a static attribute sourced from the Token ID or a dynamic one from a contract function
     *
     * If a dynamic function, then test to see if we can source the value from the result cache -
     * Test to see if the contract has seen any transactions AFTER the result was cached, if so then invalidate the result and re-fetch
     *
     * TODO: there is currently an optimisation issue with this. If the contract being called to resolve the attribute is not the token
     *       contract which the script refers to then the result is not cached. This can be seen eg with 'ENABLE' functions which permit
     *       the script contract to manipulate the tokens which the 'ENABLE' function is being called on.
     *       It may not be possible to always safely cache these values; even with an event handler we have to interpret those events and invalidate
     *       any cached results. However if we're tracking the referenced contract as a token then it should be safe
     *
     * @param walletAddress
     * @param attr
     * @param tokenId
     * @param cAddr
     * @param td
     * @param attrIf
     * @param transactionUpdate
     * @return
     */
    public Observable<TokenScriptResult.Attribute> fetchAttrResult(String walletAddress, AttributeType attr, BigInteger tokenId, ContractAddress cAddr, TokenDefinition td, AttributeInterface attrIf, long transactionUpdate)
    {
        if (attr == null) return Observable.fromCallable(() -> new TokenScriptResult.Attribute("bd", "bd", BigInteger.ZERO, ""));
        else if (attr.event != null)
        {
            //retrieve events from DB
            ContractAddress useAddress = new ContractAddress(attr.event.eventModule.contractInfo.addresses.keySet().iterator().next(),
                                                             attr.event.eventModule.contractInfo.addresses.values().iterator().next().get(0));
            TransactionResult cachedResult = attrIf.getFunctionResult(useAddress, attr, tokenId); //Needs to allow for multiple tokenIds
            return resultFromDatabase(cachedResult, attr);
        }
        else if (attr.function == null)  // static attribute from tokenId (eg city mapping from tokenId)
        {
            return staticAttribute(attr, tokenId);
        }
        else
        {
            ContractAddress useAddress;
            if (cAddr == null) useAddress = new ContractAddress(attr.function);
            else useAddress = new ContractAddress(attr.function, cAddr.chainId, cAddr.address);
            TransactionResult cachedResult = attrIf.getFunctionResult(useAddress, attr, tokenId); //Needs to allow for multiple tokenIds
            if (cAddr != null && !useAddress.address.equalsIgnoreCase(cAddr.address)) transactionUpdate = 0; //If calling a function which isn't the main tokenscript function retrieve from contract call not cache
            if (!attr.isVolatile() && (attrIf.resolveOptimisedAttr(useAddress, attr, cachedResult) || !cachedResult.needsUpdating(transactionUpdate))) //can we use wallet's known data or cached value?
            {
                return resultFromDatabase(cachedResult, attr);
            }
            else  //if cached value is invalid or if value is dynamic
            {
                return fetchResultFromEthereum(walletAddress, useAddress, attr, tokenId, td, attrIf, transactionUpdate)       // Fetch function result from blockchain
                        .map(result -> restoreFromDBIfRequired(result, cachedResult))  // If network unavailable restore value from cache
                        .map(attrIf::storeAuxData)                                     // store new data
                        .map(result -> parseFunctionResult(result, attr));    // write returned data into attribute
            }
        }
    }

    private Observable<TokenScriptResult.Attribute> staticAttribute(AttributeType attr, BigInteger tokenId)
    {
        return Observable.fromCallable(() -> {
            try
            {
                BigInteger val = tokenId.and(attr.bitmask).shiftRight(attr.bitshift);
                return new TokenScriptResult.Attribute(attr.id, attr.name, val, attr.getSyntaxVal(attr.toString(val)), attr.userInput);
            }
            catch (Exception e)
            {
                return new TokenScriptResult.Attribute(attr.id, attr.name, tokenId, "unsupported encoding");
            }
        });
    }

    private Observable<TokenScriptResult.Attribute> resultFromDatabase(TransactionResult transactionResult, AttributeType attr)
    {
        return Observable.fromCallable(() -> parseFunctionResult(transactionResult, attr));
    }

    /**
     * Restore result from Database if required (eg connection failure), and if there was a database value to restore
     * @param result return from calling contract function
     * @param cachedResult previous return value, restored from database
     * @return
     */
    private TransactionResult restoreFromDBIfRequired(TransactionResult result, TransactionResult cachedResult)
    {
        if (result.resultTime == 0 && cachedResult != null && result.result == null)
        {
            result.result = cachedResult.result;
            result.resultTime = cachedResult.resultTime;
        }

        return result;
    }

    public String convertInputValue(AttributeType attr, TokenscriptElement e, String valueFromInput)
    {
        String convertedValue = "";
        try
        {
            byte[] inputBytes;
            switch (attr.as)
            {
                //UTF8, Unsigned, Signed, Mapping, Boolean, UnsignedInput, TokenId
                case Unsigned:
                case Signed:
                case UnsignedInput:
                    inputBytes = TokenscriptFunction.convertArgToBytes(Utils.isolateNumeric(valueFromInput)); //convert cleaned user input
                    BigInteger unsignedValue = new BigInteger(inputBytes);
                    convertedValue = unsignedValue.toString();
                    e.value = unsignedValue.toString();
                    break;
                case UTF8:
                    e.value = valueFromInput;
                    convertedValue = valueFromInput;
                    break;
                case Bytes:
                    //apply bitmask to user entry and shift it because bytes is the other way round
                    inputBytes = TokenscriptFunction.convertArgToBytes(valueFromInput);
                    if (inputBytes.length <= 32)
                    {
                        BigInteger val = new BigInteger(1, inputBytes).and(attr.bitmask).shiftRight(attr.bitshift);
                        e.value = val.toString(16);
                    }
                    else
                    {
                        e.value = com.alphawallet.token.tools.Numeric.toHexString(inputBytes);
                    }
                    convertedValue = e.value;
                    break;
                case e18:
                    e.value = BalanceUtils.EthToWei(valueFromInput);
                    convertedValue = e.value;
                    break;
                case e8:
                    e.value = BalanceUtils.UnitToEMultiplier(valueFromInput, new BigDecimal("100000000"));
                    convertedValue = e.value;
                    break;
                case e4:
                    e.value = BalanceUtils.UnitToEMultiplier(valueFromInput, new BigDecimal("1000"));
                    convertedValue = e.value;
                    break;
                case e2:
                    e.value = BalanceUtils.UnitToEMultiplier(valueFromInput, new BigDecimal("100"));
                    convertedValue = e.value;
                    break;
                case Mapping:
                    //makes no sense as input
                    break;
                case Boolean:
                    //attempt to decode
                    if (valueFromInput.equalsIgnoreCase("true") || valueFromInput.equals("1"))
                    {
                        e.value = "TRUE";
                    }
                    else
                    {
                        e.value = "FALSE";
                    }
                    convertedValue = e.value;
                    break;
                case TokenId:
                    break;
            }
        }
        catch (Exception excp)
        {
            excp.printStackTrace();
            convertedValue = TOKENSCRIPT_CONVERSION_ERROR + excp.getMessage();
        }

        return convertedValue;
    }
}
