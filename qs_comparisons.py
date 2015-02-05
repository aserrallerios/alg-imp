def pivot_first(array):
    return array[0]


def pivot_last(array):
    return array[-1]


def pivot_median(array):
    a = array[0]
    b = array[-1]
    l = len(array)
    if l % 2 == 0:
        c = array[l // 2 - 1]
    else:
        c = array[l // 2]
    return sorted([a, b, c])[1]


def swap(array, i, j):
    array[j], array[i] = array[i], array[j]


def quick(array, median_func):
    if len(array) < 2:
        return array, 0
    pivot = median_func(array)
    swap(array, 0, array.index(pivot))
    i = 1
    for j in range(1, len(array)):
        if array[j] < pivot:
            swap(array, i, j)
            i += 1
    swap(array, 0, i - 1)
    comp = len(array) - 1
    # print('pivot %s left %s right %s' % (pivot, array[:i - 1], array[i:]))
    less, count = quick(array[:i - 1], median_func)
    comp += count
    more, count = quick(array[i:], median_func)
    comp += count
    # print('final count for %s is %s' % (array, comp))
    return less + [pivot] + more, comp

f = open('QuickSort.txt', 'r')
array = []
for l in f:
    array.append(int(l))
print(array)


print(quick(array[:], pivot_first))
print(quick(array[:], pivot_last))
print(quick(array[:], pivot_median))
