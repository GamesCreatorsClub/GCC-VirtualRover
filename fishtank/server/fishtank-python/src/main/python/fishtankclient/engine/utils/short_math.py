
def is_less(l, r):
    diff = l - r

    if diff > 32767:
        return True
    elif diff < -32766:
        return False

    return diff < 0


def next(s):
    if s == 65535:
        return 1

    return s + 1


def add(l, r):
    res = l - 1 + r
    if res < 0:
        res += 65535
    if res > 65534:
        res -= 65535

    return res + 1


def index_of(start, len, requested):
    if is_less(requested, start) or is_less(add(start, len - 1), requested):
        return -1

    if requested < start:
        requested += 65535

    return requested - start


def diff(l, r):
    diff = l - r

    if diff > 32767:
        return 65535 - diff
    elif diff < -32766:
        return 65535 + diff
    elif diff < 0:
        return -diff

    return diff
