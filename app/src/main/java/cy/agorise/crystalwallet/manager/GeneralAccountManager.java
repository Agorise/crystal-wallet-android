package cy.agorise.crystalwallet.manager;

import android.accounts.Account;
import android.content.Context;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.script.Script;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cy.agorise.crystalwallet.apigenerator.ApiRequest;
import cy.agorise.crystalwallet.apigenerator.ApiRequestListener;
import cy.agorise.crystalwallet.apigenerator.InsightApiGenerator;
import cy.agorise.crystalwallet.apigenerator.insightapi.models.Txi;
import cy.agorise.crystalwallet.apigenerator.insightapi.models.Vin;
import cy.agorise.crystalwallet.apigenerator.insightapi.models.Vout;
import cy.agorise.crystalwallet.application.CrystalApplication;
import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.enums.CryptoCoin;
import cy.agorise.crystalwallet.models.AccountSeed;
import cy.agorise.crystalwallet.models.BitcoinAddress;
import cy.agorise.crystalwallet.models.BitcoinTransaction;
import cy.agorise.crystalwallet.models.BitcoinTransactionGTxIO;
import cy.agorise.crystalwallet.models.CryptoCoinBalance;
import cy.agorise.crystalwallet.models.CryptoCoinTransaction;
import cy.agorise.crystalwallet.models.CryptoCurrency;
import cy.agorise.crystalwallet.models.CryptoNetAccount;
import cy.agorise.crystalwallet.models.GTxIO;
import cy.agorise.crystalwallet.models.GeneralCoinAddress;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequest;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequestsListener;
import cy.agorise.crystalwallet.requestmanagers.GeneralAccountSendRequest;
import cy.agorise.graphenej.Util;

public class GeneralAccountManager implements CryptoAccountManager, CryptoNetInfoRequestsListener {

    static HashMap<CryptoCoin, GeneralAccountManager> generalAccountManagers = new HashMap();

    private static CryptoCoin[] SUPPORTED_COINS = new CryptoCoin[]{
            CryptoCoin.BITCOIN,
            CryptoCoin.BITCOIN_TEST,
            CryptoCoin.DASH,
            CryptoCoin.LITECOIN
    } ;

    final CryptoCoin cryptoCoin;
    final Context context;

    public static GeneralAccountManager getAccountManager(CryptoCoin coin){
        return generalAccountManagers.get(coin);
    }

    public GeneralAccountManager(CryptoCoin cryptoCoin, Context context) {
        this.cryptoCoin = cryptoCoin;
        this.context = context;
        generalAccountManagers.put(cryptoCoin,this);
    }

    @Override
    public void createAccountFromSeed(CryptoNetAccount account, ManagerRequest request, Context context) {

    }

    @Override
    public void importAccountFromSeed(CryptoNetAccount account, Context context) {

    }

    @Override
    public void loadAccountFromDB(CryptoNetAccount account, Context context) {

    }

    @Override
    public void onNewRequest(CryptoNetInfoRequest request) {
        if(Arrays.asList(SUPPORTED_COINS).contains(request.getCoin())){
            if(request instanceof GeneralAccountSendRequest){
                this.send((GeneralAccountSendRequest)request);
            }else{
                System.out.println("Invalid " +this.cryptoCoin.getLabel() + " request ");
            }

        }

    }

    /**
     * Class that process each transaction fetched by the insight api
     * @param txi
     */
    public void processTxi(Txi txi){
        CrystalDatabase db = CrystalDatabase.getAppDatabase(this.context);
        List<BitcoinTransaction> btTransactions = db.bitcoinTransactionDao().getTransactionsByTxid(txi.txid);
        if(!btTransactions.isEmpty()){
            for(BitcoinTransaction btTransaction : btTransactions) {
                btTransaction.setConfirmations(txi.confirmations);
                CryptoCoinTransaction ccTransaction = db.transactionDao().getById(btTransaction.getCryptoCoinTransactionId());
                if (!ccTransaction.isConfirmed() && btTransaction.getConfirmations() >= cryptoCoin.getCryptoNet().getConfirmationsNeeded()) {
                    ccTransaction.setConfirmed(true);
                    db.transactionDao().insertTransaction(ccTransaction);
                    updateBalance(ccTransaction,(ccTransaction.getInput()?1:-1)*ccTransaction.getAmount(),db);
                }

                db.bitcoinTransactionDao().insertBitcoinTransaction(btTransaction);
            }
        }else {
            /*List<CryptoCoinTransaction> ccTransactions = new ArrayList();
            btTransactions = new ArrayList();*/ //TODO transactions involving multiples accounts
            CryptoCoinTransaction ccTransaction = new CryptoCoinTransaction();
            BitcoinTransaction btTransaction = new BitcoinTransaction();
            btTransaction.setTxId(txi.txid);
            btTransaction.setBlock(txi.blockheight);
            btTransaction.setFee((long) (txi.fee * Math.pow(10, cryptoCoin.getPrecision())));
            btTransaction.setConfirmations(txi.confirmations);
            ccTransaction.setDate(new Date(txi.time * 1000));
            if(txi.txlock || txi.confirmations >= cryptoCoin.getCryptoNet().getConfirmationsNeeded()) {
                ccTransaction.setConfirmed(true);
            }else{
                ccTransaction.setConfirmed(false);
            }

            ccTransaction.setInput(false);

            long amount = 0;


            //transaction.setAccount(this.mAccount);
            //transaction.setType(cryptoCoin);
            List<BitcoinTransactionGTxIO> gtxios = new ArrayList();
            for (Vin vin : txi.vin) {
                BitcoinTransactionGTxIO input = new BitcoinTransactionGTxIO();
                String addr = vin.addr;
                input.setAddress(addr);
                input.setIndex(vin.n);
                input.setOutput(true);
                input.setAmount((long) (vin.value * Math.pow(10, cryptoCoin.getPrecision())));
                input.setOriginalTxId(vin.txid);
                input.setScriptHex(vin.scriptSig.hex);

                BitcoinAddress address = db.bitcoinAddressDao().getdadress(addr);
                if(address != null){
                    if(ccTransaction.getAccountId() < 0){
                        ccTransaction.setAccountId(address.getAccountId());
                        ccTransaction.setFrom(addr);
                        ccTransaction.setInput(false);
                    }

                    if(ccTransaction.getAccountId()== address.getAccountId()){
                        amount -= vin.value;
                    }
                }

                if(ccTransaction.getFrom() == null || ccTransaction.getFrom().isEmpty()){
                    ccTransaction.setFrom(addr);
                }

                gtxios.add(input);


            }

            for (Vout vout : txi.vout) {
                if (vout.scriptPubKey.addresses == null || vout.scriptPubKey.addresses.length <= 0) {

                } else {
                    BitcoinTransactionGTxIO output = new BitcoinTransactionGTxIO();
                    String addr = vout.scriptPubKey.addresses[0];
                    output.setAddress(addr);
                    output.setIndex(vout.n);
                    output.setOutput(false);
                    output.setAmount((long) (vout.value * Math.pow(10, cryptoCoin.getPrecision())));
                    output.setScriptHex(vout.scriptPubKey.hex);
                    output.setOriginalTxId(txi.txid);

                    gtxios.add(output);
                    BitcoinAddress address = db.bitcoinAddressDao().getdadress(addr);
                    if(address != null){
                        if(ccTransaction.getAccountId() < 0){
                            ccTransaction.setAccountId(address.getAccountId());
                            ccTransaction.setInput(true);
                            ccTransaction.setTo(addr);
                        }

                        if(ccTransaction.getAccountId()== address.getAccountId()){
                            amount += vout.value;
                        }
                    }else{
                        //TOOD multiple send address
                        if(ccTransaction.getTo() == null || ccTransaction.getTo().isEmpty()){
                            ccTransaction.setTo(addr);
                        }
                    }
                }
            }

            ccTransaction.setAmount(amount);
            CryptoCurrency currency = db.cryptoCurrencyDao().getByNameAndCryptoNet(this.cryptoCoin.name(), this.cryptoCoin.getCryptoNet().name());
            if (currency == null) {
                currency = new CryptoCurrency();
                currency.setCryptoNet(this.cryptoCoin.getCryptoNet());
                currency.setName(this.cryptoCoin.name());
                currency.setPrecision(this.cryptoCoin.getPrecision());
                long idCurrency = db.cryptoCurrencyDao().insertCryptoCurrency(currency)[0];
                currency.setId(idCurrency);
            }

            ccTransaction.setIdCurrency((int)currency.getId());

            long ccId = db.transactionDao().insertTransaction(ccTransaction)[0];
            btTransaction.setCryptoCoinTransactionId(ccId);
            long btId = db.bitcoinTransactionDao().insertBitcoinTransaction(btTransaction)[0];
            for(BitcoinTransactionGTxIO gtxio : gtxios){
                gtxio.setBitcoinTransactionId(btId);
                db.bitcoinTransactionDao().insertBitcoinTransactionGTxIO(gtxio);
            }

            if(ccTransaction.isConfirmed()) {
                updateBalance(ccTransaction,amount,db);
            }
        }
    }

    private void updateBalance(CryptoCoinTransaction ccTransaction, long amount, CrystalDatabase db){
        CryptoCurrency currency = db.cryptoCurrencyDao().getByNameAndCryptoNet(this.cryptoCoin.name(), this.cryptoCoin.getCryptoNet().name());
        if (currency == null) {
            currency = new CryptoCurrency();
            currency.setCryptoNet(this.cryptoCoin.getCryptoNet());
            currency.setName(this.cryptoCoin.name());
            currency.setPrecision(this.cryptoCoin.getPrecision());
            long idCurrency = db.cryptoCurrencyDao().insertCryptoCurrency(currency)[0];
            currency.setId(idCurrency);
        }

        CryptoCoinBalance balance = db.cryptoCoinBalanceDao().getBalanceFromAccount(ccTransaction.getAccountId(), currency.getId());
        if (balance == null) {
            balance = new CryptoCoinBalance();
            balance.setAccountId(ccTransaction.getAccountId());
            balance.setCryptoCurrencyId(currency.getId());
            long idBalance = db.cryptoCoinBalanceDao().insertCryptoCoinBalance(balance)[0];
            balance.setId(idBalance);
        }
        balance.setBalance(balance.getBalance()+amount);
        db.cryptoCoinBalanceDao().insertCryptoCoinBalance(balance);
    }

    public void send(final GeneralAccountSendRequest request){
        //TODO check server connection
        //TODO validate to address

        InsightApiGenerator.getEstimateFee(this.cryptoCoin,new ApiRequest(1, new ApiRequestListener() {
            @Override
            public void success(Object answer, int idPetition) {
                Transaction tx = new Transaction(cryptoCoin.getParameters());
                long currentAmount = 0;
                long fee = -1;
                long feeRate = (Long) answer;
                fee = 226 * feeRate;

                CrystalDatabase db = CrystalDatabase.getAppDatabase(request.getContext());
                db.bitcoinTransactionDao();

                List<BitcoinTransactionGTxIO> utxos = getUtxos(request.getAccount().getId(),db);

                if(currentAmount< request.getAmount() + fee){
                    request.setStatus(GeneralAccountSendRequest.StatusCode.NO_BALANCE);
                    return;
                }
                AccountSeed seed = db.accountSeedDao().findById(request.getAccount().getSeedId());
                DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey((DeterministicKey) seed.getPrivateKey(),
                        new ChildNumber(44, true));
                DeterministicKey coinKey = HDKeyDerivation.deriveChildKey(purposeKey,
                        new ChildNumber(cryptoCoin.getCoinNumber(), true));
                DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinKey,
                        new ChildNumber(request.getAccount().getAccountIndex(), true));
                DeterministicKey externalKey = HDKeyDerivation.deriveChildKey(accountKey,
                        new ChildNumber(0, false));
                DeterministicKey changeKey = HDKeyDerivation.deriveChildKey(accountKey,
                        new ChildNumber(1, false));

                //String to an address
                Address toAddr = Address.fromBase58(cryptoCoin.getParameters(), request.getToAccount());
                tx.addOutput(Coin.valueOf(request.getAmount()), toAddr);

                /*if(request.getMemo()!= null && !request.getMemo().isEmpty()){
                    String memo = request.getMemo();
                    if(request.getMemo().length()>40){
                        memo = memo.substring(0,40);
                    }
                    byte[]scriptByte = new byte[memo.length()+2];
                    scriptByte[0] = 0x6a;
                    scriptByte[1] = (byte) memo.length();
                    System.arraycopy(memo.getBytes(),0,scriptByte,2,memo.length());
                    Script memoScript = new Script(scriptByte);
                    tx.addOutput(Coin.valueOf(0),memoScript);
                }*/

                //Change address
                long remain = currentAmount - request.getAmount() - fee;
                if( remain > 0 ) {
                    long index = db.bitcoinAddressDao().getLastChangeAddress(request.getAccount().getId());
                    BitcoinAddress btAddress = db.bitcoinAddressDao().getChangeByIndex(index);
                    Address changeAddr;
                    if(btAddress != null && db.bitcoinTransactionDao().getGtxIOByAddress(btAddress.getAddress()).size()<=0){
                            changeAddr = Address.fromBase58(cryptoCoin.getParameters(), btAddress.getAddress());

                    }else{
                        if(btAddress == null){
                            index = 0;
                        }else{
                            index++;
                        }
                        btAddress = new BitcoinAddress();
                        btAddress.setIndex(index);
                        btAddress.setAccountId(request.getAccount().getId());
                        btAddress.setChange(true);
                        btAddress.setAddress(HDKeyDerivation.deriveChildKey(changeKey, new ChildNumber((int) btAddress.getIndex(), false)).toAddress(cryptoCoin.getParameters()).toString());
                        db.bitcoinAddressDao().insertBitcoinAddresses(btAddress);
                        changeAddr = Address.fromBase58(cryptoCoin.getParameters(), btAddress.getAddress());
                    }
                    tx.addOutput(Coin.valueOf(remain), changeAddr);
                }

                for(BitcoinTransactionGTxIO utxo: utxos) {
                    Sha256Hash txHash = Sha256Hash.wrap(utxo.getOriginalTxId());
                    Script script = new Script(Util.hexToBytes(utxo.getScriptHex()));
                    TransactionOutPoint outPoint = new TransactionOutPoint(cryptoCoin.getParameters(), utxo.getIndex(), txHash);
                    BitcoinAddress btAddress = db.bitcoinAddressDao().getdadress(utxo.getAddress());
                    ECKey addrKey;

                    if(btAddress.isChange()){
                        addrKey = HDKeyDerivation.deriveChildKey(changeKey, new ChildNumber((int) btAddress.getIndex(), false));
                    }else{
                        addrKey = HDKeyDerivation.deriveChildKey(changeKey, new ChildNumber((int) btAddress.getIndex(), true));
                    }
                    tx.addSignedInput(outPoint, script, addrKey, Transaction.SigHash.ALL, true);
                }

                InsightApiGenerator.broadcastTransaction(cryptoCoin,Util.bytesToHex(tx.bitcoinSerialize()),new ApiRequest(1, new ApiRequestListener() {
                    @Override
                    public void success(Object answer, int idPetition) {
                        request.setStatus(GeneralAccountSendRequest.StatusCode.SUCCEEDED);
                    }

                    @Override
                    public void fail(int idPetition) {
                        request.setStatus(GeneralAccountSendRequest.StatusCode.PETITION_FAILED);
                    }
                }));
            }

            @Override
            public void fail(int idPetition) {
                request.setStatus(GeneralAccountSendRequest.StatusCode.NO_FEE);

            }
        }));
    }

    private List<BitcoinTransactionGTxIO> getUtxos(long accountId, CrystalDatabase db){
        List<BitcoinTransactionGTxIO> answer = new ArrayList<>();
        List<BitcoinTransactionGTxIO> bTGTxI = new ArrayList<>();
        List<BitcoinTransactionGTxIO> bTGTxO = new ArrayList<>();
        List<CryptoCoinTransaction> ccTransactions = db.transactionDao().getByIdAccount(accountId);
        for(CryptoCoinTransaction ccTransaction : ccTransactions) {
            List<BitcoinTransactionGTxIO> gtxios = db.bitcoinTransactionDao().getGtxIOByTransaction(ccTransaction.getId());
            for(BitcoinTransactionGTxIO gtxio : gtxios){
                if(db.bitcoinAddressDao().addressExists(gtxio.getAddress())){
                    if(gtxio.isOutput()){
                        bTGTxO.add(gtxio);
                    }else{
                        bTGTxI.add(gtxio);
                    }
                }
            }
        }
        for(BitcoinTransactionGTxIO gtxi : bTGTxI){
            boolean find = false;
            for(BitcoinTransactionGTxIO gtxo : bTGTxO){
                if(gtxo.getOriginalTxId().equals(gtxi.getOriginalTxId())){
                    find = true;
                    break;
                }
            }
            if(!find){
                answer.add(gtxi);
            }
        }

        return answer;
    }
}
