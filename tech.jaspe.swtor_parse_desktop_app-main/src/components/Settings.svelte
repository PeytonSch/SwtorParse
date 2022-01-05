<div class="Layout Layout--gutter-none p-4">
	<div class="Layout-main">
		<!-- Subhead -->
		<div class="Subhead border-0">
			<div class="Subhead-heading">Preferences</div>
		</div>

		<nav class="UnderlineNav">
			<div class="UnderlineNav-body" role="menu">
				<button class="UnderlineNav-item" role="tab" type="button">General</button>
				<button class="UnderlineNav-item" role="tab" type="button" aria-selected="true">DoT/HoT Tracker</button>
			</div>
		</nav>

		<form>
			<div class="form-group">
				<div class="form-group-header">
					<label for="tracker-name">Name</label>
				</div>
				<div class="form-group-body">
					<input class="form-control" type="text" placeholder="My Affliction Tracker" id="tracker-name" />
				</div>
			</div>

			<div class="form-group">
				<div class="form-group-header">
					<label for="ability-id">Ability ID</label>
				</div>
				<div class="form-group-body">
					<input class="form-control" type="text" placeholder="808192586022912" id="ability-id" />
				</div>
			</div>

			<div class="form-group">
				<div class="form-group-header">
					<label for="example-textarea">Example Textarea</label>
				</div>
				<div class="form-group-body">
					<textarea class="form-control" id="example-textarea" />
				</div>
			</div>

			<div class="form-actions">
				<button type="submit" class="btn btn-primary">Save changes</button>
				<button type="button" class="btn">Cancel</button>
			</div>
		</form>
	</div>
</div>
