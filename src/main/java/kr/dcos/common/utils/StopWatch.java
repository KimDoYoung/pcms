package kr.dcos.common.utils;

import java.util.concurrent.TimeUnit;

public class StopWatch {
	    
	    /**
	     * 스톱워치 시작 시간.
	     */
	    private long startTime = -1;
	    /**
	     * 스톱워치 중단 시간.
	     */
	    private long stopTime = -1;

	    /**
	     * <p>생성자. 별도로 하는 일은 없음.</p>
	     */
	    public StopWatch() {
	    }

	    /**
	     * <p>스톱워치를 시작한다.</p>
	     * 
	     * <p>새로운 계시(計時)를 시작하며 이전에 시작한 값이 있다면 모두 초기화된다.</p>
	     */
	    public void start() {
	        stopTime = -1;
	        startTime = System.currentTimeMillis();
	    }

	    /**
	     * <p>스톱워치를 중단한다.</p>
	     * 
	     * <p>새로운 계시(計時)를 시작하며, 경과한 시간을 기록한다.</p>
	     */
	    public void stop() {
	        stopTime = System.currentTimeMillis();
	    }

	    /**
	     * <p>스톱워치를 리셋한다.</p>
	     * 
	     * <p>이전 기록에 따른 내부 설정값을 모두 초기화한다.</p>
	     */
	    public void reset() {
	        startTime = -1;
	        stopTime = -1;
	    }

	    /**
	     * <p>시간을 분할한다.</p>
	     * 
	     * <p>분할 시간을 얻기 위해 중단시간을 설정한다.<br>
	     * 시작시간과는 무관하며, 원 시작점에서 계시(計時)를 
	     * 지속하기 위해 {@link #unsplit()}를 통해 활성화 한다.</p>
	     */
	    public void split() {
	        stopTime = System.currentTimeMillis();
	    }

	    /**
	     * <p>분할한 시간정보를 제거한다.</p>
	     * 
	     * <p>이 함수는 중단시간을 초기화한다.<br> 
	     * 시작시간과는 무관하며, 원 시작점에서 계시(計時)를
	     * 지속하기 위해 활성화 한다.</p>
	     */
	    public void unsplit() {
	        stopTime = -1;
	    }

	    /**
	     * <p>추후 재시작을 위해 스톱워치를 일시 중단한다.</p>
	     */
	    public void suspend() {
	        stopTime = System.currentTimeMillis();
	    }

	    /**
	     * <p>일시 중단된 스톱워치를 다시 시작한다.</p>
	     * 
	     * <p>일시중단전에 기록된 시작시간은 이 함수 시점에 더해져서 시작된다.</p>
	     */
	    public void resume() {
	        startTime += (System.currentTimeMillis() - stopTime);
	        stopTime = -1;
	    }

	    /**
	     * <p>스톱워치에 따른 경과시간을 밀리초로 얻는다.</p>
	     * 
	     * <p>반환값은 start()와 가장 최근의 split() 사이 이거나, 
	     * start()와 stop() 사이이거나,
	     * start()와 본 함수가 호출된 순간 들 중에 하나이다.</p>
	     * 
	     * @return 경과한 밀리초
	     */
	    public long getTime() {
	        if (stopTime == -1) {
	            if (startTime == -1) {
	                return 0;
	            }
	            return (System.currentTimeMillis() - this.startTime);
	        }
	        return (this.stopTime - this.startTime);
	    }

	    /**
	     * <p>스톱워치 기록시간을 문자열로 얻는다.</p>
	     * 
	     * <p>문자열은 ISO8601 포맷으로 반환한다.
	     * <i>시</i>:<i>분</i>:<i>초</i>.<i>밀리초</i>.</p>
	     * 
	     * @return 기록시간 문자열
	     */
	    @Override
		public String toString() {
	        long millis = getTime(); 
	        int hour = (int)((millis / (1000*60*60))%24);
	        int min = (int)((millis / (1000*60))%60);
	        return String.format("%dms (%02d:%02d:%02d.%03d)",
	        		millis,
	        		hour,
	        		min,
	        		TimeUnit.MILLISECONDS.toSeconds(millis),
	        		(millis%1000));
	    }

}
