//Comment
function check(x) {
    if (typeof x !== 'number' || x <= 0)
        return "invalid input";
    var result;
    switch (x) {
        case 1:
        case 2:
            result = "one or two";
            break;
        case 3:
            result = "three";
            break;
        case 4:
            result = "four";
            break;
        default:
            result = "unsupported";
    }
    return result
}

function aORbANDc(a, b, c) {
    if ((a || b) && c)
        return true;
    else;
    return false;
}