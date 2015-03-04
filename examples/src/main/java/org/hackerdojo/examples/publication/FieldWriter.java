package org.hackerdojo.examples.publication;

final class FieldWriter implements Runnable {

    @Override
    public void run() {
        while (true) {
            Main.checker = new SanityChecker(3);
        }
    }
}
