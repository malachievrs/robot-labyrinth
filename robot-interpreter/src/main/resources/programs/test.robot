seisu kansu fib(seisu n) kido
    sorenara n < 2 kido
        return n;
    shushi;
    seisu a = fib(n - 1);
    seisu b = fib(n - 2);
    return a + b;
shushi;
seisu result = fib(5);
