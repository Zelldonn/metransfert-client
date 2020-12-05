import com.packeteer.network.Packet;

public interface TransactionListener {
    void onTransactionStart();
    void onTransactionFinish(TransactionResult result);

    class TransactionResult{
        public final Packet packet;

        public TransactionResult(Packet p) {
            this.packet = p;
        }
    }
}
