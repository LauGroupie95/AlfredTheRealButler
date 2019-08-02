/* Generated by AN DISI Unibo */ 
package it.unibo.timer

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Timer ( name: String, scope: CoroutineScope ) : ActorBasicFsm( name, scope){
 	
	override fun getInitialState() : String{
		return "s0"
	}
		
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		var ActualTimer = 0L 
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("Start timer")
						surpluss.timerSupport.create(myself)
					}
					 transition( edgeName="goto",targetState="ready", cond=doswitch() )
				}	 
				state("ready") { //this:State
					action { //it:State
						ActualTimer = 0L 
					}
					 transition(edgeName="t035",targetState="start",cond=whenDispatch("setTimer"))
				}	 
				state("start") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("setTimer(DURATION)"), Term.createTerm("setTimer(DURATION)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								ActualTimer = payloadArg(0).toLong()
						}
						surpluss.timerSupport.startTimer( ActualTimer  )
					}
					 transition(edgeName="t036",targetState="reset",cond=whenDispatch("resetTimer"))
					transition(edgeName="t037",targetState="drinnn",cond=whenDispatch("internalTickTimer"))
				}	 
				state("reset") { //this:State
					action { //it:State
						surpluss.timerSupport.resetTimer(  )
					}
					 transition( edgeName="goto",targetState="ready", cond=doswitch() )
				}	 
				state("drinnn") { //this:State
					action { //it:State
						forward("tickTimer", "tickTimer(ok)" ,"onecellforward" ) 
					}
					 transition( edgeName="goto",targetState="ready", cond=doswitch() )
				}	 
			}
		}
}
