function validCode(code) {
    if (code !== undefined && code !== null && code !== '') {
        return true;
    }
    return false;
}

function aORbANDc(a, b, c) {
 if ((a || b) && c)
     return true;
 else;
    return false;
}
