function inversions(array) {
  if (array.length === 0) return {sortedArray: [], inversions: 0};
  if (array.length === 1) return {sortedArray: [parseInt(array[0])], inversions: 0};
  var leftArray = inversions(array.slice(0,(array.length/2)));
  var rightArray = inversions(array.slice((array.length/2), array.length));
  return function(left, right) {
      var i = 0;
      var j = 0;
      var inversions = 0;
      var leftArray = left.sortedArray;
      var rightArray = right.sortedArray;
      var leftLength = leftArray.length;
      var sortedArray = [];
      while (i < leftLength) {
            if (!rightArray[j]) {
                    sortedArray.push(leftArray[i++]);
                  } else if (leftArray[i] <= rightArray[j]) {
                          sortedArray.push(leftArray[i++]);
                        } else {
                                sortedArray.push(rightArray[j++]);
                                inversions += leftLength - i;
                              }
          }
      while (j < rightArray.length) {
            sortedArray.push(rightArray[j++]);
          }
      return {sortedArray: sortedArray,inversions: left.inversions + right.inversions + inversions};
    }(leftArray, rightArray);
}
