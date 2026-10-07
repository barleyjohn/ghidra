/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ghidra.app.plugin.core.decompile.actions;

import docking.action.MenuData;
import ghidra.app.decompiler.*;
import ghidra.app.plugin.core.decompile.DecompilerActionContext;
import ghidra.program.model.pcode.*;
import ghidra.program.model.symbol.SourceType;

public class IsolateExpressionAction extends AbstractDecompilerAction {

	public IsolateExpressionAction() {
		super("Split Out As New Expression");
		//setHelpLocation(new HelpLocation(HelpTopics.DECOMPILER, "ActionIsolate"));
		setPopupMenuData(new MenuData(new String[] { "Split Out As New Expression" }, "Decompile"));
	}

	@Override
	protected boolean isEnabledForDecompilerContext(DecompilerActionContext context) {
		ClangToken tokenAtCursor = context.getTokenAtCursor();
		if (!(tokenAtCursor instanceof ClangOpToken)) {
			return false;
		}
		ClangOpToken opToken = (ClangOpToken) tokenAtCursor;
		PcodeOp op = opToken.getPcodeOp();
		// Is this even possible?
		if (op == null) {
			return false;
		}
		Varnode ovn = op.getOutput();
		// Require output varnode to split on
		if (ovn == null) {
			return false;
		}
		HighVariable hv = ovn.getHigh();
		// All HighVariables except HighOther have associated storage already
		if (!(hv instanceof HighOther)) {
			return false;
		}
		return true;
	}

	@Override
	protected void decompilerActionPerformed(DecompilerActionContext context) {
		IsolateExpressionTask newExprTask =
			new IsolateExpressionTask(context.getTool(),context.getProgram(),
				context.getComponentProvider(),(ClangOpToken) context.getTokenAtCursor(),SourceType.USER_DEFINED);

		newExprTask.runTask(false);
	}

}
