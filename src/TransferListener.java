public interface TransferListener extends TransactionListener{

    void onTransferUpdate(Info info);
    class Info{
        public final int expectedBytes;
        public final int transferredBytes;
        public final float throughput;

        public Info(int expectedBytes, int transferredBytes, float throughput) {
            this.expectedBytes = expectedBytes;
            this.transferredBytes = transferredBytes;
            this.throughput = throughput;
        }
    }
}
