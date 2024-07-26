@extends('layouts.app')

@section('content')
<div class="container">
    <h1>Challenges</h1>
    <a href="{{ route('challenges.create') }}" class="btn btn-primary">Add New Challenge</a>

    @if(session('success'))
        <div class="alert alert-success">{{ session('success') }}</div>
    @endif

    <table class="table">
        <thead>
            <tr>
                <th>Challenge No</th>
                <th>Challenge Name</th>
                <th>Attempt Duration</th>
                <th>Number of Questions</th>
                <th>Overall Mark</th>
                <th>Open Date</th>
                <th>Close Date</th>
            </tr>
        </thead>
        <tbody>
            @foreach($challenges as $challenge)
                <tr>
                    <td>{{ $challenge->challengeNo }}</td>
                    <td>{{ $challenge->challengeName }}</td>
                    <td>{{ $challenge->attemptDuration }}</td>
                    <td>{{ $challenge->noOfQuestions }}</td>
                    <td>{{ $challenge->overallMark }}</td>
                    <td>{{ $challenge->openDate }}</td>
                    <td>{{ $challenge->closeDate }}</td>
                </tr>
            @endforeach
        </tbody>
    </table>
</div>
@endsection