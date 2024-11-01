import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;

public class LeetCode {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
//        System.out.print("Nhập s :");

//        System.out.println(romanToInt("MCMXCIV"));  // Kết quả: 1994
        int[] nums = {1, 2, 3, 1, 1, 3};
        System.out.println(numIdenticalPairs(nums));
        System.out.println("alo alo");

        // ahihi


    }

    // Number of Good Pair
    public static int numIdenticalPairs(int[] nums) {
        Map<Integer,Integer> map = new HashMap<>();
        int result = 0;
        for (int num : nums) {
            int count = map.getOrDefault(num, 0);
            result += count;
            map.put(num, count + 1);
        }
        return result;
    }

    // Reverse Integer
    public static int reverse(int x) {
        int rs = 0;
        String xStr = Integer.toString(x);
        xStr = xStr.replace("-","");
        String xStrReverse = new StringBuilder(xStr).reverse().toString();
        try{
            rs = Integer.parseInt(xStrReverse);
            if(x < 0){
                rs = -rs;
            }
            return rs;
        }catch (Exception ex){
            return 0;
        }

    }

    // Palindrome Number
    public static boolean isPalindrome(Integer x) {
        String strX = x.toString();
        String strReverse = "";
        for(int i = strX.length() - 1; i >=0; i--){
            strReverse += strX.charAt(i);
        }
        if(strX.equals(strReverse)){
            return true;
        }
        return false;
    }
    //Roman to Integer
    //Roman numerals are represented by seven different symbols: I, V, X, L, C, D and M.
    public static int romanToInt(String s) {
        Map<Character, Integer> romanValues = new HashMap<>();
        romanValues.put('I', 1);
        romanValues.put('V', 5);
        romanValues.put('X', 10);
        romanValues.put('L', 50);
        romanValues.put('C', 100);
        romanValues.put('D', 500);
        romanValues.put('M', 1000);

        int total = 0;
        int prevValue = 0;

        for (int i = s.length() - 1; i >= 0; i--) {
            int currentValue = romanValues.get(s.charAt(i));

            if (currentValue < prevValue) {
                total -= currentValue;
            } else {
                total += currentValue;
            }
            prevValue = currentValue;
        }
        return total;
    }

    //10. Regular Expression Matching
    public boolean isMatch(String s, String p) {
        return false;
    }
}
