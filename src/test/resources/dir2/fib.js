function fib(n) {
    if (n == 0)
        return 1;
    if (n == 1)
        return 1;
    return n + fib(n - 1);
}