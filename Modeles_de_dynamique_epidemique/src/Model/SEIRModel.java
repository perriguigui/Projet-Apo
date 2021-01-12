package Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SEIRModel implements Model{

        Map<String, Double> params = new HashMap<String, Double>();
        private Board board;
        private String[] states = {"Number of susceptible", "Number of exposed", "Number of infected", "Number of recovered"};

        public SEIRModel(){
            this.params.put("sigma",70/100.0);
            this.params.put("beta",80/100.0);
            this.params.put("gamma",60/100.0);
        }

        public String[] getStates() {  return states; }

    /**
     * @param board
     */
        public void setBoard(Board board){ this.board = board; }

        public void initActors(){ board.modifyActors(this.params);}

        public void setModelParams(Map<String, Double> params ){ this.params=params; }

        public int[] numberOfPeople(){
            return new int[] { board.numberOfHealthy(), board.numberOfExposed(), board.numberOfSick(), board.numberOfCured()};
        }

        public int[] stepInfection(){
            board.move();
            this.infect();
            return numberOfPeople();
        }


        public void infect() {
            List<Set<Actor>> sets = board.find();
            for (Set<Actor> as : sets) {
                List<Actor> healthy = board.getHealthy(as);
                List<Actor> sick = board.getSick(as);
                List<Actor> exposed = board.getExposed(as);

                for (Actor a: healthy){
                    if (doExpose(a)) {
                        a.setState(Actor.State.EXPOSED);
                    }
                }

                for (Actor a : sick) {

                    if (doInfect(a)) {
                        setAll(exposed, Actor.State.SICK);
                    }

                    if (doCure(a)) {
                        a.setState(Actor.State.IMMUNE);
                    }

                }
            }
        }

        public void setAll(List<Actor> as, Actor.State state) {
            for (Actor a : as) {
                a.setState(state);
            }
        }

        public boolean doExpose(Actor a){
            double sigma = getActorSigma(a);
            return Math.random() < sigma;
        }

        public boolean doInfect(Actor a){
            double beta = getActorBeta(a);
            return Math.random() < beta;
        }

        public boolean doCure(Actor a){
            double gamma = getActorGamma(a);
            return Math.random() < gamma;
        }

    /**
     * @param a
     * @return
     */
        public double getActorGamma(Actor a){ return a.getParams().get("gamma"); }

    /**
     * @param a
     * @return
     */
        public double getActorBeta(Actor a){ return a.getParams().get("beta"); }

    /**
     * @param a
     * @return
     */
        public double getActorSigma(Actor a){ return a.getParams().get("sigma"); }

    /**
     * @param board
     */
        public void addBoard(Board board){ this.board = board; }

        public Map<String, Double> getParams(){return this.params;}
}
