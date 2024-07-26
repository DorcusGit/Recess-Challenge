@extends('layouts.app')

@section('content')
<div class="container">
    <h1>Create Challenge</h1>

    <form action="{{ route('challenges.store') }}" method="POST">
        @csrf

        <div class="mb-3">
            <label for="challengeNo" class="form-label">Challenge No</label>
            <input type="text" class="form-control" id="challengeNo" name="challengeNo" required>
        </div>

        <div class="mb-3">
            <label for="challengeName" class="form-label">Challenge Name</label>
            <input type="text" class="form-control" id="challengeName" name="challengeName" required>
        </div>

        <div class="mb-3">
            <label for="attemptDuration" class="form-label">Attempt Duration (HH:MM:SS)</label>
            <input type="time" class="form-control" id="attemptDuration" name="attemptDuration" required>
        </div>

        <div class="mb-3">
            <label for="noOfQuestions" class="form-label">Number of Questions</label>
            <input type="number" class="form-control" id="noOfQuestions" name="noOfQuestions" required>
        </div>

        <div class="mb-3">
            <label for="overallMark" class="form-label">Overall Mark</label>
            <input type="number" class="form-control" id="overallMark" name="overallMark" required>
        </div>

        <div class="mb-3">
            <label for="openDate" class="form-label">Open Date</label>
            <input type="date" class="form-control" id="openDate" name="openDate" required>
        </div>

        <div class="mb-3">
            <label for="closeDate" class="form-label">Close Date</label>
            <input type="date" class="form-control" id="closeDate" name="closeDate" required>
        </div>

        <button type="submit" class="btn btn-success">Submit</button>
    </form>
</div>
@endsection