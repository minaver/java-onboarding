package onboarding;

import java.util.*;
import java.util.stream.Collectors;

/**
 * >> 기능 요구 사항 정리
 * 0. 기본 조건
 * - 친구 추천 규칙에 따라 점수가 가장 높은 순으로 정렬하여 최대 5명을 return
 *      - 사용자와 함께 아는 친구의 수 = 10점
 *      - 사용자의 타임 라인에 방문한 횟수 = 1점
 *
 * 1. 접근 방식
 *  - 사용자와 친구간 그래프를 만들어 사용자를 기준으로 BFS 탐색 2depth 실행
 *      - 1 depth : 사용자의 친구들
 *      - 2 depth : 사용자와 동일한 친구를 아는 사람들 <- 해당 사람들에게 +10
 *  - visitor는 최종적으로 더함
 *
 * */
public class Problem7 {

    private HashMap<String, List<String>> friendGraph = new HashMap<>();
    private HashMap<String, Integer> friendScore = new HashMap<>();

    public static List<String> solution(String user, List<List<String>> friends, List<String> visitors) {
        Problem7 problem7 = new Problem7();

        // Friend Graph 제작
        problem7.makeFriendGraph(friends);

        // 친구 추천 규칙에 따라 점수 계산
        problem7.calcScoreFriends(user);
        problem7.calcScoreVisitor(visitors);

        // user와 user의 직접적인 친구는 추천에서 제외
        problem7.removeUserFriend(user);

        // 1순위 정렬 : 추천 점수 - 2순위 정렬 : 이름 => 구현은 역순으로 먼저 이름으로 정렬하고 이후 추천 점수로 정렬
        return problem7.friendScore.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .filter(f -> f.getValue() > 0)
                .map(f -> f.getKey())
                .collect(Collectors.toList());
    }

    // user와 user의 직접적인 친구는 추천에서 제외
    public void removeUserFriend(String user){
        friendScore.remove(user);
        for(String userFriend : friendGraph.get(user)){
            friendScore.remove(userFriend);
        }
    }

    // 사용자와 함께 아는 친구의 수로 score 계산(10점)
    public void calcScoreFriends(String user){
        List<String> userFriendList = friendGraph.get(user);

        for(String userFriend : userFriendList){
            for(String scoredFriend : friendGraph.get(userFriend)){
                friendScore.put(scoredFriend,(friendScore.get(scoredFriend) + 10));
            }
        }
    }

    // 사용자의 타임 라인에 방문한 횟수로 score 계산(1점)
    public void calcScoreVisitor(List<String> visitors) {
        for (String visitor : visitors) {
            // 이미 friend에서 추가된 crew일 경우
            if (friendScore.containsKey(visitor)) {
                friendScore.put(visitor, (friendScore.get(visitor) + 1));
                continue;
            }

            // friend에서 추가되지 않은 신규 crew일 경우
            friendScore.put(visitor, 1);
        }
    }

    // HashMap으로 FriendGraph 구성
    public void makeFriendGraph(List<List<String>> friends){
        for(List<String> crew : friends){
            boolean isFirstCrewExist = friendGraph.containsKey(crew.get(0));
            boolean isSecondCrewExist = friendGraph.containsKey(crew.get(1));

            if(isFirstCrewExist && isSecondCrewExist) {
                // 둘다 그래프에 있을시
                friendGraph.get(crew.get(0)).add(crew.get(1));
                friendGraph.get(crew.get(1)).add(crew.get(0));
                continue;
            }
            if(!isFirstCrewExist){
                // 첫번째 친구가 Graph에 없을시
                friendGraph.put(crew.get(0),new ArrayList<>());
                friendScore.put(crew.get(0),0);
            }
            if(!isSecondCrewExist){
                // 두번째 친구가 Graph에 없을시
                friendGraph.put(crew.get(1),new ArrayList<>());
                friendScore.put(crew.get(1),0);
            }
            friendGraph.get(crew.get(0)).add(crew.get(1));
            friendGraph.get(crew.get(1)).add(crew.get(0));
        }
    }
    
}
